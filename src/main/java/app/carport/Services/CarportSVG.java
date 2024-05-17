package app.carport.Services;

public class CarportSVG {
    private final SVGDrawer carportSvg;
    private final double width;
    private final double length;

    public CarportSVG(double width, double length) {
        this.width = width;
        this.length = length;
        carportSvg = new SVGDrawer(0, 0, "0 0 855 690", "75%");
        carportSvg.addRectangle(0, 0, width, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addText(length / 2, -10, String.format("%.2f cm", length), "fill:black; font-size:12px;");
        carportSvg.addText(-30, width / 2, String.format("%.2f cm", width), "fill:black; font-size:12px; transform:rotate(-90, -30, " + width / 2 + ");");
        addBeams();
        addRafters();
        addArrows();
        addPost();
    }

    private void addBeams() {
        carportSvg.addRectangle(0, 35, 4.5, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(0, width - 30.15, 4.5, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters() {
        for (double i = 0; i < length; i += 59.5) {
            carportSvg.addRectangle(i, 0.0, width, 4.5, "stroke:#000000; fill: #ffffff");
        }
    }

    private void addArrows() {
        carportSvg.addArrow(0, 0, length, width, "stroke: black; stroke-width: 2;");
        carportSvg.addArrow(0, width, length, 0, "stroke: black; stroke-width: 2;");
    }

    private void addPost() {
        double postWidth = 9.7;
        double remWidth = 4.5;
        int distanceToEdge = 100;
        double remDistance = 35;
        if (length < 500) {
            carportSvg.addRectangle(distanceToEdge, remDistance - (remWidth / 2), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            carportSvg.addRectangle(length - (distanceToEdge + postWidth), remDistance - (remWidth / 2), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");

            carportSvg.addRectangle(distanceToEdge, width - (remDistance - (remWidth / 2)), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            carportSvg.addRectangle(length - (distanceToEdge + postWidth), width - (remDistance - (remWidth / 2)), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        } else {
            carportSvg.addRectangle(distanceToEdge, remDistance - (remWidth / 2), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            carportSvg.addRectangle(length - (distanceToEdge + postWidth), remDistance - (remWidth / 2), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            carportSvg.addRectangle(length / 2 - (postWidth / 2), remDistance - (remWidth / 2), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");

            carportSvg.addRectangle(distanceToEdge, width - (remDistance - (remWidth / 2)), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            carportSvg.addRectangle(length - (distanceToEdge + postWidth), width - (remDistance - (remWidth / 2)), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            carportSvg.addRectangle(length / 2 - (postWidth / 2), width - (remDistance - (remWidth / 2)), postWidth, postWidth, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
