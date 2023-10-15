import java.util.ArrayList;

import processing.core.PApplet;

public class MatrixRain extends PApplet
{
    ArrayList<Text> Flow= new ArrayList<Text>();
    boolean Pressed = false;
    int r = 0, g = 255, b = 0, a = 0;


    public static void main(String[] args)
    {
        PApplet.main("MatrixRain");
    }

    public void settings()
    {
        size(800,800);
    }

    public void setup()
    {

    }

    public void draw()
    {
        screenImage(40,10,100);

    }

    public void mousePressed()
    {
        Pressed = true;
    }

    public void mouseReleased()
    {
        if(Pressed)
        {
            Flow.add(0,new Text(mouseX,mouseY-300,(int)random(0,100)));
            Flow.get(0).generateDisplayedText();
        }

        Pressed = false;
    }

    public void keyPressed()
    {
        if(a == 0)
        {
            r = 255;
            g = 0;
            b = 0;
        }
        else if (a == 1) {
            r = 0;
            g = 255;
            b = 0;
        } else if(a == 2) {
            r = 0;
            g = 0;
            b = 255;
        }

        a++;

        if(a > 2)
            a = 0;
    }

    public void screenImage(int totalLines, int min, int max)
    {
        while(Flow.size()<totalLines)
        {
            Flow.add(0,new Text( (int) random(0,width),(int) random(-500,-1000),(int)random(min,max)));
            Flow.get(0).generateDisplayedText();
            Flow.get(0).setMotionSpeed((int) random(5,20));
            Flow.get(0).setColor(r, g, b);
            //Flow.get(0).setColor((int) random(0,255), (int) random(0,255), (int) random(0,255));
        }
        background(0);
        for(int i=0; i<Flow.size(); i++)
        {

            Flow.get(i).display();
        }
        for(int i=0; i<Flow.size(); i++)
        {
            Flow.get(i).move();
            if(Flow.get(i).getY() > Flow.get(i).getLength()*Flow.get(i).getTextSize())
            {
                Flow.remove(i);
            }
        }
    }

    class Text
    {
        String usableChar = "";
        String displayedString = "";
        int x , y , length, textSize=20, motionSpeed = textSize;
        int r = 0 , g = 255, b = 0;

        public Text(int x, int y, int length)
        {
            this.x=x;
            this.y=y;
            this.length=length;
        }

        public Text(int x, int y, int length, String ... letters)
        {
            this.x=x;
            this.y=y;
            this.length=length;
            for(int i=0; i<letters.length; i++)
            {
                usableChar = usableChar +letters[i];
            }
        }

        public void generateDisplayedText()
        {
            if(usableChar.equals(""))
            {
                while(displayedString.length()<=length)
                {
                    int h= (int) (Math.random() *((100 - 30) + 1) + 30);
                    displayedString = displayedString + Character.toString((char) h);
                }
            }
            else
            {
                while(displayedString.length()<length)
                {
                    displayedString = displayedString + usableChar.charAt((int) random(0,usableChar.length()));
                }
            }
        }

        public void display()
        {
            int a=y;
            fill(r,g,b);
            for(int i=0; i<length; i++)
            {
                textSize(textSize);
                text(displayedString.charAt(i),x,a);
                a=a+motionSpeed;
            }
        }

        public void move()
        {
            y=y+motionSpeed;
        }

        public void Debug()
        {
            System.out.println("__________Debugging Info__________");
            System.out.println("X position: "+ x + "   Y postion: " + y);
            System.out.println("Displayed String Length: " + length);
            System.out.println("Usable Letters:          " + usableChar);
            System.out.println("Letters to be Displayed: " + displayedString);
            System.out.println("__________Debugging ends__________");

        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public int getLength()
        {
            return length;
        }

        public int getTextSize()
        {
            return textSize;
        }

        public String getDisplayedString()
        {
            return displayedString;
        }

        public String getUsableChar()
        {
            return usableChar;
        }

        public void setX(int x)
        {
            this.x=x;
        }

        public void setY(int y)
        {
            this.y=y;
        }

        public void setColor(int r, int g, int b)
        {
            this.r=r;
            this.g=g;
            this.b=b;
        }

        public void setMotionSpeed(int speed)
        {
            motionSpeed = speed;
        }

        public void setLength(int length)
        {
            this.length=length;
        }

        public void setTextSize(int size)
        {
            textSize = size;
        }

        public void setDisplayedString(String a)
        {
            displayedString = a;
        }

        public void setUsableChar(String a)
        {
            usableChar = a;
        }
    }

}
