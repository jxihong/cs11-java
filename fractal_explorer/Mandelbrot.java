import java.awt.geom.Rectangle2D;


/**
 * Computes the Mandelbrot fractal
 */
public class Mandelbrot extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;

    public void getInitialRange(Rectangle2D.Double range) {
	range.x = -2;
	range.y = -1.5;

	range.width = 3;
	range.height = 3;
    }

    /**
     * For the Mandelbrot fractal, the function is z_n = z_(n-1) ^ 2 + c, 
     * where all values are complex numbers, z0 = 0, and c is the 
     * particular point in the fractal that we are displaying. This
     * computation is iterated until either |z| > 2 (in which case the 
     * point is not in the Mandelbrot set), or until the number of 
     * iterations hits a maximum value, e.g. 2000 (in which case we assume 
     * the point is in the set).
     */

    public int numIterations(double x, double y) {
	int count = 0;
	
	double re = 0;
	double im = 0;
	double z_n2 = 0;
	
	while (count < MAX_ITERATIONS && z_n2 < 4) {
	    count++;
	    
	    double nextRe = Math.pow(re, 2) - Math.pow(im, 2) + x;
	    double nextIm = 2 * re * im + y;

	    z_n2 = Math.pow(nextRe, 2) + Math.pow(nextIm, 2);

	    re = nextRe;
	    im = nextIm;	   
	}

	return count < MAX_ITERATIONS ? count : -1;
    }
    
    public static String getString() {
	return "Mandelbrot";
    }
}