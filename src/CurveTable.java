import java.util.ArrayList;

import processing.core.PApplet;

public class CurveTable extends PApplet
{
    ArrayList<Circle> row = new ArrayList<Circle>();
    ArrayList<Circle> col = new ArrayList<Circle>();
    public static void main (String[] args)
    {
        PApplet.main("CurveTable");
    }

    public void settings()
    {
        size(2000,800);
        //fullScreen();
    }

    public void setup()
    {
        stroke(255,255,0);
        noFill();
        background(0);
        for(int i = 150; i< width; i = i+75)
        {
            row.add(row.size(),new Circle(i,75,50));
            col.add(col.size(),new Circle(75,i,50));
        }

        for(int i=0; i< row.size(); i++)
        {
            row.get(i).drawCircle();
            col.get(i).drawCircle();
            row.get(i).setSpeed((i+1)*0.01f);
            col.get(i).setSpeed((i+1)*0.01f);
        }


    }

    public void draw()
    {
        stroke(255,255,0);
        fill(255,0,0);
        rect(0,0,width,110);
        rect(0,0,110,width);
        rect(0,0,110,110);


        for(int i =0; i<row.size(); i++)
        {
            strokeWeight(1);
            row.get(i).drawCircle();
            col.get(i).drawCircle();
            strokeWeight(10);
            row.get(i).drawPoint();
            col.get(i).drawPoint();
            row.get(i).movePoint();
            col.get(i).movePoint();
        }

        for(int i=0; i<row.size();i++)
        {
            for(int j=0; j<col.size(); j++)
            {
                strokeWeight(1);
                point(row.get(i).getPointY(),col.get(j).getPointX());
            }
        }


    }

    public void mousePressed()
    {
        background(0);
    }

    class Circle
    {
        private int x, y, diameter;
        private float px, py, angle, speed;
        int red, green, blue ;

        public Circle(int x, int y, int diameter)
        {
            this.x=x;
            this.y=y;
            this.diameter=diameter;
        }

        public void drawCircle()
        {
            ellipse(x,y,diameter,diameter);
        }

        public void drawPoint()
        {
            px = getRadius() * cos(angle - PI/2) + y;
            py = getRadius() * sin(angle - PI/2) + x;
            point(py,px);
        }

        public void movePoint()
        {
            angle += speed;
        }

        public float getPointX()
        {
            return px;
        }

        public float getPointY()
        {
            return py;
        }

        public int getDiameter()
        {
            return diameter;
        }

        public float getRadius()
        {
            return (diameter/2);
        }

        public void setSpeed(float speed)
        {
            this.speed=speed;
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
            red = r;
            green = g;
            blue = b;
        }

        public float getAngle()
        {
            return angle;
        }

        public void setAngle(float angle)
        {
            this.angle=angle;
        }

        public void clear(int red, int green, int blue)
        {
            noStroke();
            fill(red,green,blue);
            rectMode(CENTER);
            rect(x,y,diameter,diameter);
        }

        public double toDegrees()
        {
            return angle*180/PI;
        }

        public void drawAll()
        {
            drawCircle();
            drawPoint();
        }
    }
}

