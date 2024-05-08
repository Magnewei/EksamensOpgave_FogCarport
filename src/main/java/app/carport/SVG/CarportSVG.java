package app.carport.SVG;

public class CarportSVG {
    private final SVGDrawer carportSvg;
    private double width;
    private double length;

    public CarportSVG(double width, double length) {
        this.width=width;
        this.length=length;
        carportSvg = new SVGDrawer(0, 0, "0 0 855 690", "75%");
        carportSvg.addRectangle(0,0,width, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        addBeams();
        addRafters();
        addArrows();
        addPost();

    }

    // TODO: Fix konstanter i metoderne til at være baseret på width og height.
    private void addBeams() {
        carportSvg.addRectangle(0,35,4.5, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(0,width-35,4.5, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters() {
        for (double i = 59.5/2; i < length; i+= 59.5) {
            carportSvg.addRectangle(i, 0.0, width, 4.5,"stroke:#000000; fill: #ffffff" );
        }
    }

    private void addArrows(){
        carportSvg.addArrow(0, 0, length, width, "stroke: black; stroke-width: 2;");
        carportSvg.addArrow(0, width, length, 0, "stroke: black; stroke-width: 2;");
    }

    private void addPost(){
        carportSvg.addRectangle(100,32.4,9.7, 9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(length-100,32.4,9.7, 9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }



    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
