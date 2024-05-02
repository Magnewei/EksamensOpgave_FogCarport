package app.carport.SVG;

public class CarportSVG {
    private final int width;
    private final int length;
    private final SVGDrawer carportSvg;

    public CarportSVG(int width, int length) {
        this.width = width;
        this.length = length;
        carportSvg = new SVGDrawer(0, 0, "0 0 855 690", "75%" );
        carportSvg.addRectangle(0,0,600, 780, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        addBeams();
        addRafters();
    }

    // TODO: Fix konstanter i metoderne til at være baseret på width og height.
    private void addBeams() {
        carportSvg.addRectangle(0,35,4.5, 780, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(0,565,4.5, 780, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters() {
        for (double i = 0; i < 780; i+= 55.714) {
            carportSvg.addRectangle(i, 0.0, 600, 4.5,"stroke:#000000; fill: #ffffff" );
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
