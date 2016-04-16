package a5jedi;

import java.util.Iterator;

abstract public class AnyFrame2D implements Frame2D {

	abstract public int getWidth();
	abstract public int getHeight();
	abstract public Pixel getPixel(int x, int y);
	abstract public Frame2D setPixel(int x, int y, Pixel p);
	
    public Pixel getPixel(Coordinate c) {
    	return this.getPixel(c.getX(), c.getY());
    }
    
    public Frame2D setPixel(Coordinate c, Pixel p) {
    	return this.setPixel(c.getX(), c.getY(), p);
    }

	public Frame2D lighten(double factor) {
		Frame2D result = this;
		for (int x=0; x<getWidth(); x++) {
			for (int y=0; y<getHeight(); y++) {
				result = result.setPixel(x,y,getPixel(x,y).lighten(factor));
			}
		}
		return result;
	}

	public Frame2D darken(double factor) {
		if ((factor < 0.0 || factor > 1.0)) {
			throw new IllegalArgumentException("Factor out of range");
		}

		Frame2D result = this;
		for (int x=0; x<getWidth(); x++) {
			for (int y=0; y<getHeight(); y++) {
				result = result.setPixel(x,y,getPixel(x,y).darken(factor));
			}
		}
		return result;
	}		

	public IndirectFrame2D extract(int xOffset, int yOffset, int width, int height) {
		IndirectFrame2D frame = new IndirectFrame2DImpl(this, xOffset, yOffset, width, height);
		return frame;
	}
	
    public IndirectFrame2D extract(Coordinate corner_a, Coordinate corner_b) {
		return this.extract(corner_a.getX(), corner_a.getY(), corner_b.getX() - corner_a.getX(), corner_b.getY() - corner_a.getY());
    }
    
    public Iterator<Pixel> iterator() {
    	Iterator<Pixel> it = new PixelIterator(this);
    	return it;
    }
    
    public Iterator<Pixel> sample(int init_x, int init_y, int dx, int dy) {
    	check_coordinates(init_x, init_y);
    	if (init_x + dx > this.getHeight() || init_y + dy > this.getWidth()) {
    		throw new IllegalArgumentException("Delta out of range");
    	}
    	Iterator<Pixel> it = new PixelIterator(this, init_x, init_y, dx, dy);
    	return it;
    }
    
    public Iterator<IndirectFrame2D> window(int window_width, int window_height) {
    	check_coordinates(window_width, window_height);
    	Iterator<IndirectFrame2D> it = new Frame2DIteratorWindow(this, window_width, window_height);
    	return it;
    }
    
    //TODO
    public Iterator<IndirectFrame2D> tile(int tile_width, int tile_height) {
    	check_coordinates(tile_width, tile_height);
    	Iterator<IndirectFrame2D> it = new Frame2DIteratorTile(this, tile_width, tile_height);
    	return it;
    }

	public Iterator<Pixel> zigzag() {
		return new PixelIteratorZigZag(this);
	}
    
	protected boolean check_coordinates(int x, int y) {
		if (x < 0 || x >= getWidth()) {
			throw new RuntimeException("x is out of bounds");
		}
		if (y < 0 || y >= getHeight()) {
			throw new RuntimeException("y is out of bounds");
		}
		return true;
	}

	protected static boolean check_factor(double factor) {
		if (factor < 0) {
			throw new RuntimeException("Factor can not be less than 0.0");
		} else if (factor > 1.0) {
			throw new RuntimeException("Factor can not be greater than 1.0");
		}
		return true;
	}

}
