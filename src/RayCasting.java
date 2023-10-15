import java.util.ArrayList;

import processing.core.PApplet;

public class RayCasting extends PApplet
{
    public static void main(String[] args)
    {
        PApplet.main("RayCasting");
    }

    public void settings()
    {
        size(600,600);
    }

    ArrayList<Wall> walls = new ArrayList<Wall>();
    Particle p = new Particle(100,300,360);

    public void setup()
    {
        buildWalls();
        background(0);
        fill(255);
    }

    public void draw()
    {
        background(0);
        stroke(255,255,0);
        walls.forEach(w -> w.render());
        stroke(0,255,0,90);
        p.render(walls);
        p.setXY(mouseX, mouseY);
    }

    public void mousePressed()
    {
        buildWalls();
    }

    public void buildWalls()
    {
        walls = new ArrayList<Wall>();

        for(int i=0; i<4; i++)
        { walls.add(new Wall(random(0,width),random(0,height),random(0,width),random(0,height))); }

        walls.add(new Wall(0,0,0,height));
        walls.add(new Wall(0,0,width,0));
        walls.add(new Wall(width,0,width,height));
        walls.add(new Wall(0,height,width,height));
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
