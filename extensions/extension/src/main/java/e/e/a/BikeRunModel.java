package e.e.a;

import java.util.ArrayList;
import java.util.Random;

/** Original, offline bicycle runner physics in logical screen units. */
public final class BikeRunModel {
    public static final float RIDER_X = 120, GRAVITY = 1600, JUMP = -650;
    public static final class Hazard {
        public float x;
        public final float width, height;
        public final boolean gap;
        Hazard(float x, float width, float height, boolean gap) { this.x=x; this.width=width; this.height=height; this.gap=gap; }
    }
    public final ArrayList<Hazard> hazards = new ArrayList<>();
    private final Random random;
    public float y, velocity, distance, spawn;
    public boolean started, over;
    public BikeRunModel(long seed) { random = new Random(seed); reset(); }
    public void reset() { hazards.clear(); y=velocity=distance=0; spawn=700; started=over=false; }
    public void tap() {
        if (over) reset();
        started=true;
        if (y==0) velocity=JUMP;
    }
    public void step(float dt) {
        if (!started || over || dt<=0) return;
        dt=Math.min(dt, .035f);
        float movement=(260+Math.min(distance/100, 150))*dt;
        distance+=movement; spawn-=movement;
        velocity+=GRAVITY*dt; y+=velocity*dt;
        if (y>0) { y=0; velocity=0; }
        if (spawn<=0) {
            boolean gap=random.nextBoolean();
            hazards.add(new Hazard(760, gap ? 115+random.nextInt(40) : 36+random.nextInt(22), gap ? 0 : 28+random.nextInt(27), gap));
            spawn=350+random.nextInt(220);
        }
        for (int n=hazards.size()-1; n>=0; n--) {
            Hazard h=hazards.get(n); h.x-=movement;
            boolean hit=h.gap ? RIDER_X>=h.x && RIDER_X<=h.x+h.width && y>=-2 :
                RIDER_X+26>h.x && RIDER_X-26<h.x+h.width && y>-h.height-8;
            if (hit) over=true;
            if (h.x+h.width<0) hazards.remove(n);
        }
    }
    public int score() { return (int)(distance/10); }
}
