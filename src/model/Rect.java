package model;

public class Rect {
   public Point p1;
   public Point p2;
   
   public Rect() {
      p1 = new Point();
      p2 = new Point();
   }

   public Rect(int x1, int y1, int x2, int y2) {
      p1 = new Point(x1, y1);
      p2 = new Point(y1, y2);
   }
}
