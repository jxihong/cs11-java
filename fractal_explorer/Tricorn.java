import java.awt.geom.Rectangle2D;

/**
 * Computes the Tricorn fractal
 */
public class Tricorn extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;

    public void getInitialRange(Rectangle2D.Double range) {
	range.x = -2;
	range.y = -2;

	range.width = 4;
	range.height = 4;
    }

    /**
     * Like the Mandelbrot fractal, only it takes the complex conjugate
     * of z_(n-1) instead
     */

    public int numIterations(double x, double y) {
	int count = 0;
	
	double re = 0;
	double im = 0;
	double z_n2 = 0;

	while (count < MAX_ITERATIONS && z_n2 < 4) {
	    count++;

	    //Takes the conjugate squared
	    double nextRe = Math.pow(re, 2) - Math.pow(im, 2) + x;
            double nextIm = -2 * re * im + y;
 
	    z_n2 = Math.pow(nextRe, 2) + Math.pow(nextIm, 2);
	
	    re = nextRe;
	    im = nextIm;
	}

	return count < MAX_ITERATIONS ? count : -1;
    }
    
    public static String getString() {
	return "Tricorn";
    }
}