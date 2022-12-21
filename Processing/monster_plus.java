import ddf.minim.*;

Minim minim;
AudioInput audio;


// orange

public float volume;


int numPoints1 = 1000;
PVector[] points1 = new PVector[numPoints1];
PVector center = new PVector(width / 2, height / 2);
PFont font;
int numPoints = 1000;
PVector[] points = new PVector[numPoints];

ArrayList < Stream > streams; // an array to hold the streams of characters
String symbols = "10\u2605 \u2620 \u2622 \u2623 \u2626 \u262A \u262E \u262F \u2638 \u2639 \u263A \u2648 \u2649 \u264A \u264B \u264C \u264D \u264E \u264F \u2650 \u2651 \u2652 \u2653 \u265F \u2660 \u2663 \u2665 \u2666 \u2668 \u267B \u267E \u2692 \u2693 \u2694 \u2695 \u2696 \u2697 \u2699 \u269B \u269C \u26A0 \u26A1 \u26AA \u26AB \u26B0 \u26B1 \u26BD \u26BE \u26C4 \u26C5 \u26C8 \u26CE \u26CF \u26D1 \u26D3 \u26D4 \u26E9 \u26EA \u26F0 \u26F1 \u26F2 \u26F3 \u26F4 \u26F5 \u26F7 \u26F8 \u26F9 \u26FA \u26FD \u2702 \u2705 \u2708 \u2709 \u270A \u270B \u270C \u270F \u2712 \u2714 \u2716 \u2728 \u2733 \u2734 \u2744 \u2747 \u274C \u274E \u2753 \u2754 \u2755 \u2757 \u2763 \u2764 \u2795 \u2796 \u2797 \u27A1 \u27B0 \u27BF \u2934 \u2935 \u2B05 \u2B06 \u2B07 \u2B1B \u2B1C \u2B50 \u2B55 \u3030 \u303D \u3297 \u3299"; // the characters and symbols to use
float speed;
void setup() {
    size(1920, 1080);
    background(0); // set the background to black
    fill(0, 255, 0); // set the text color to green
    textSize(16); // set the text size to 16 pixels

    font = createFont("Arial Unicode MS", 16);
    textFont(font);
    // create the streams
    streams = new ArrayList < Stream > ();
    for (int i = 0; i < width / textWidth("W"); i++) {
        streams.add(new Stream(color(0, 255, 0), color(255, 128, 0)));
    }
    for (int i = 0; i < numPoints; i++) {
        points[i] = new PVector(random(0, width), random(0, height));
    }

    minim = new Minim(this);
    audio = minim.getLineIn();
}

void draw() {
    background(0, 150); // set the background to a transparent black

    // draw the streams
    for (Stream stream: streams) {
        stream.render();
    }


    for (int i = 0; i < numPoints; i++) {
        // Find closest points
        PVector closest1 = null;
        PVector closest2 = null;
        float closest1Distance = Float.MAX_VALUE;
        float closest2Distance = Float.MAX_VALUE;
        for (int j = 0; j < numPoints; j++) {
            if (i != j) {
                float distance = dist(points[i].x, points[i].y, points[j].x, points[j].y);
                if (distance < closest1Distance) {
                    closest2 = closest1;
                    closest2Distance = closest1Distance;
                    closest1 = points[j];
                    closest1Distance = distance;
                } else if (distance < closest2Distance) {
                    closest2 = points[j];
                    closest2Distance = distance;
                }
            }
        }

        // Draw lines and triangle to closest points

        volume = audio.left.level() * .15;
        stroke(0, 255, 0);
        line(points[i].x, points[i].y, closest1.x, closest1.y);
        line(points[i].x, points[i].y, closest2.x, closest2.y);

        // Draw triangle
        fill(0, 0, 0);
        triangle(points[i].x, points[i].y, closest1.x, closest1.y, closest2.x, closest2.y);

        // Smoothly interpolate between current position and new random position
        points[i].x = lerp(points[i].x, random(0, width), -volume);
        points[i].y = lerp(points[i].y, random(0, height), -volume);

        if (points[i].x < 0 || points[i].x > width || points[i].y < 0 || points[i].y > height) {
            // Reset point's position to the center of the canvas
            points[i].x = random(800, 1020);
            points[i].y = random(380, 700);
        }
    }
}

// a Stream object to hold the characters
class Stream {
    float x; // the x position of the stream
    float y; // the y position of the stream
    // the speed of the stream
    int length; // the length of the stream
    ArrayList < Character > characters; // an array to hold the characters in the stream
    float opacity; // the opacity of the stream
    float symbolSize;
    float[] symbolDistances;
    color startColor;
    color endColor;
    int totalSymbols;
    float angle = 0;
    Stream(color startColor, color endColor) {
        this.startColor = color(0, 255, 0); // green
        this.endColor = color(255, 128, 0);
        symbolSize = random(10, 30);
        totalSymbols = round(random(5, 30));
        symbolDistances = new float[totalSymbols];

        x = random(width); // a random x position for the stream
        y = random(-10, -100); // a random starting y position for the stream
        speed = random(1.2, 17); // a random speed for the stream
        length = (int) random(15, 420); // a random length for the stream
        characters = new ArrayList < Character > (); // create the characters array
        opacity = random(0.5, 1); // a random starting opacity for the stream

        // populate the characters array with random characters and symbols
        for (int i = 0; i < length; i++) {
            char character = symbols.charAt((int) random(symbols.length()));
            characters.add(new Character(character, x, y, opacity));
            y -= textWidth("W"); // move the y position up for the next character
        }
    }

    // render the stream of characters
    void render() {
        // draw the characters
        for (int i = 0; i < totalSymbols; i++) {
            // calculate the x and y positions of the character
            float y = map(i, 0, totalSymbols, 0, height);
            float x = map(sin(i + angle), -1, 1, 0, width);
            float distance = dist(x, y, points[i].x, points[i].y);
            if (distance < 500) {
                fill(255, 0, 0);
            } else {
                fill(0, 255, 0); // otherwise, set it to green
            }
            text(symbols.charAt(i), x, y);
            // update the position of the character
            y -= 1;
            // interpolate the fill color based on the y position of the character
            fill(lerpColor(startColor, endColor, (y + totalSymbols * symbolSize) / height));

            // draw the character
            text(symbols.charAt(int(random(0, symbols.length()))), x, y);

        }
        for (Character character: characters) {
            character.render();
        }
        // update the angle for the next frame
        //angle += speed;
    }
}

// a Character object to hold a single character
class Character {
    char character; // the character to display
    float x; // the x position of the character
    float y; // the y position of the character
    float opacity; // the opacity of the character
    color startColor;
    color endColor;
    Character(char character, float x, float y, float opacity) {
        this.startColor = color(0, 255, 0); // green
        this.endColor = color(255, 128, 0);
        this.character = character;
        this.x = x;
        this.y = y;
        this.opacity = opacity;
    }

    // render the character
    void render() {
        for (int i = 0; i < 2; i++) {
            // calculate the x and y positions of the character
            float y = map(i, 0, 2, 0, height);
            float x = map(sin(i + 0), -1, 1, 0, width);
            float distance = dist(x, y, points[i].x, points[i].y);
            if (distance < 5) {
                fill(255, 0, 0, opacity * 255);
            } else {
                fill(lerpColor(startColor, endColor, (y) / height), opacity * 255); // set the text color and opacity
            }
        }
        fill(lerpColor(startColor, endColor, (y) / height), opacity * 255); // set the text color and opacity

        text(character, x, y);
        opacity -= 0.0005; // decrease the opacity slightly
        y += 2 * random(.75, 1.25) + (volume * 1000); // move the character down the screen
        if (y > height) { // if the character has reached the bottom of the screen
            y = random(-1000, -100); // move it back to a random starting position
            x = random(0, 1920); // move it back to a random starting position
            opacity = random(0.5, 1); // set a new random starting opacity
        }
    }
}