import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;

import java.util.Arrays;
import java.util.*;

public class Video extends PApplet
{
    private Capture video;
    public int threshold;

    ArrayList<Wall> walls = new ArrayList<Wall>();
    Particle p = new Particle(100,300,90);

    public static void main(String[] args)
    {
        System.out.println("Start");
        PApplet.main("Video");
    }

    public void settings()
    {
        size(640, 480);
    }

    public void setup()
    {
        threshold = 4;
        System.out.println("Running Setup");
        String[] cameras = new String[0];
        while(cameras.length == 0) {
            cameras = Capture.list();
            System.out.println("Here: " + Arrays.toString(cameras));
        }
        video = new Capture(this, cameras[0]);
        video.start();
    }

    public PImage grayScale(PImage input) {
        input.loadPixels();
        for (int x = 0; x < input.width; x++) {
            for (int y = 0; y < input.height; y++ ) {
                int loc = x + y*input.width;
                double average = (red(input.pixels[loc]) + green(input.pixels[loc]) + blue(input.pixels[loc])) / 3;
                input.pixels[loc] = color((int)average, (int)average, (int)average);
            }
        }
        input.updatePixels();
        return input;
    }

    public PImage inverse(PImage input) {
        input.loadPixels();
        for (int x = 0; x < input.width; x++) {
            for (int y = 0; y < input.height; y++ ) {
                int loc = x + y*input.width;
                input.pixels[loc] = color(255 - red(input.pixels[loc]), 255 - green(input.pixels[loc]), 255 - blue(input.pixels[loc]));
            }
        }
        input.updatePixels();
        return input;
    }

    double[][] edge_det = { { 0,  1, 0 }, { 1, -4, 1 }, { 0,  1, 0 } };

    double[][] blur = {{1.0/9, 1.0/9, 1.0/9}, {1.0/9, 1.0/9, 1.0/9}, {1.0/9, 1.0/9, 1.0/9}};

    double[][] identity = {{0, 0, 0}, {0, 1, 0}, {0, 0, 0}};

    double[][] sharpen = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};

    double[][] ones = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};

    int[][] deltas = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {1, -1}};

    public void draw() {

        walls = new ArrayList<Wall>(); 

        walls.add(new Wall(0,0,0,height));
        walls.add(new Wall(0,0,width,0));
        walls.add(new Wall(width,0,width,height));
        walls.add(new Wall(0,height,width,height));       

        if(video.available())
            video.read();

        image(convolve(sobel(convolve(video, blur), edge_det), sharpen), 0, 0);

        stroke(255, 255, 0);
        p.render(walls);
        p.setXY(mouseX, mouseY);

    }



   

    private int applyKernel(PImage img, int x, int y, double[][] matrix, int matrixsize)
    {
        float rtotal = 0.0f;
        float gtotal = 0.0f;
        float btotal = 0.0f;
        int offset = matrixsize / 2;
        for (int i = 0; i < matrixsize; i++){
            for (int j= 0; j < matrixsize; j++){
            int xloc = x+i-offset;
            int yloc = y+j-offset;
            int loc = xloc + img.width*yloc;
            loc = constrain(loc,0,img.pixels.length-1);
            rtotal += (red(img.pixels[loc]) * matrix[i][j]);
            gtotal += (green(img.pixels[loc]) * matrix[i][j]);
            btotal += (blue(img.pixels[loc]) * matrix[i][j]);
            }
        }
        // Make sure RGB is within range
        rtotal = constrain(rtotal, 0, 255);
        gtotal = constrain(gtotal, 0, 255);
        btotal = constrain(btotal, 0, 255);
        // Return the resulting color
        return color(rtotal, gtotal, btotal);
    }

    public PImage convolve(PImage input, double[][] kernel)
    {
        if(kernel[0].length != kernel.length)
            System.out.println("Error");        


        PImage img = createImage(input.width, input.height, RGB);
    
        input.loadPixels();    
        img.loadPixels();

        for (int x = 1; x < input.width - 1; x++)
            for(int y = 1; y < input.height - 1; y++)
                img.pixels[x + y * img.width] = applyKernel(input, x, y, kernel, kernel.length);

        img.updatePixels();
        return img;
    }

    
    public PImage sobel(PImage input, double[][] kernel)
    {
        if(kernel[0].length != kernel.length)
            System.out.println("Error");        


        PImage img = createImage(input.width, input.height, RGB);
        
        input.filter(GRAY);
        input.loadPixels();
        
        img.loadPixels();

        for (int x = 1; x < input.width - 1; x++)
        {
            for(int y = 1; y < input.height - 1; y++)
            {
                double value = red(applyKernel(input, x, y, kernel, kernel.length));
            
                if(value > threshold) {
                    img.pixels[x + y * img.width] = color(255, 255, 255);
                    walls.add(new Wall(x, y - 5, x, y + 5));
                } else {
                    img.pixels[x + y * img.width] = color(0, 0, 0);
                }
            }
        }

        img.updatePixels();
        return img;
    }

    public void keyPressed()
    {
        switch(key)
        {
            case 'q':
                System.out.println("|===== Camera Data =====|");
                System.out.println("Width: " + video.width);
                System.out.println("Height: " + video.height);
                break;
            case 'w':
                threshold += 1;
                System.out.println(threshold);
                break;
            case 's':
                threshold -= 1;
                System.out.println(threshold);
                break;
        }
    }



    class Particle
    {
        double x,y;
        ArrayList<Ray> r;
        public Particle(double x, double y, int rays)
        {
            this.x = x;
            this.y = y;
            r = new ArrayList<Ray>(rays);
            for(int i=0; i<rays; i++)
                r.add(new Ray(x,y,360/rays*i));
        }

        public void render(Wall w)
        {
            for(Ray current : r)
            {
                double[] position = current.cast(w);
                if(position != null)
                    line((float)x,(float)y,(float)position[0],(float)position[1]);
            }
        }

        public void render(ArrayList<Wall> walls)
        {
            for(Ray current : r)
            {
                double min = Double.MAX_VALUE;
                double[] minPoint = null;

                for(Wall w: walls)
                {
                    double[] position = current.cast(w);

                    if(position == null)
                        continue;

                    double distance = dist((float)position[0],(float)position[1],(float)x,(float)y);
                    if(distance < min)
                    {
                        min = distance;
                        minPoint = position;
                    }
                }
                if(minPoint != null)
                    line((float)x,(float)y,(float)minPoint[0],(float)minPoint[1]);
            }
        }

        public void setXY(double x, double y)
        {
            this.x = x;
            this.y = y;
            for(Ray current : r)
                current.setXY(x, y);
        }
    }

    class Wall
    {
        public double startX, startY, endX, endY;

        public Wall(double startX, double startY, double endX, double endY)
        {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public void render()
        { line((float)startX, (float)startY, (float) endX, (float) endY); }
    }

    class Ray
    {
        public double startX, startY, endX, endY;
        private double angle;
        private double magnitude = 1;
        public Ray(double startX, double startY, double angle)
        {
            this.startX = startX;
            this.startY = startY;
            this.angle = angle;
            calculate2ndPositon();
        }

        public void calculate2ndPositon()
        {
            endX = Math.cos(Math.toRadians(angle))*magnitude + startX;
            endY = Math.sin(Math.toRadians(angle))*magnitude + startY;
        }

        public void render()
        { line((float)startX, (float)startY, (float) endX, (float) endY); }

        public void setAngle(double angle)
        {
            this.angle = angle;
            calculate2ndPositon();
        }

        public double getAngle()
        { return angle; }

        public void incrementAngle(double amount)
        {
            angle += amount;
            calculate2ndPositon();
        }

        public double[] cast(Wall w)
        {
            double den = (w.startX - w.endX) * (this.startY -  this.endY) - (w.startY - w.endY) * (this.startX - this.endX);

            if(den == 0)
                return null;

            double t = ((w.startX - this.startX) * (this.startY -  this.endY) - (w.startY - this.startY) * (this.startX - this.endX)) / den;
            double u = -((w.startX - w.endX) * (w.startY - this.startY) - (w.startY - w.endY) * (w.startX - this.startX))             / den;

            if(t > 0 && t < 1 && u > 0)
                return new double[] {w.startX + t * (w.endX - w.startX),w.startY + t * (w.endY - w.startY)};
            else
                return null;
        }

        public void setXY(double x, double y)
        {
            this.startX = x;
            this.startY = y;
            calculate2ndPositon();
        }
    }

}