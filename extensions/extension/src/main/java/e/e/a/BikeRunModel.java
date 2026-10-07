package e.e.a;

import java.util.ArrayList;
import java.util.Random;

/** Original, offline bicycle runner physics in logical screen units. */
public final class BikeRunModel {
    public static final float RIDER_X = 120, GRAVITY = 1600, JUMP = -650;
    public static final float START_SPEED = 440, EDGE_GRACE = .10f;
    public static final class Hazard {
        public float x;
        public final float width, height;
        public final boolean gap;
        public final boolean spikes;
        Hazard(float x, float width, float height, boolean gap) { this(x,width,height,gap,false); }
        Hazard(float x, float width, float height, boolean gap, boolean spikes) { this.x=x; this.width=width; this.height=height; this.gap=gap; this.spikes=spikes; }
    }
    public final ArrayList<Hazard> hazards = new ArrayList<>();
    private final Random random;
    public float y, velocity, distance, spawn;
    private float gapTime;
    private int jumpsUsed;
    public boolean started, over;
    public BikeRunModel(long seed) { random = new Random(seed); reset(); }
    public void reset() { hazards.clear(); y=velocity=distance=gapTime=0; spawn=700; jumpsUsed=0; started=over=false; }
    // Hills, platforms and steps use one profile for both drawing and collisions.
    public static float terrain(float worldX) {
        if (worldX<=600) return 0;
        float phase=(worldX-600)%2000;
        if (phase<300) return -110*phase/300;
        if (phase<520) return -110;
        if (phase<820) return -110+110*(phase-520)/300;
        if (phase<1080) return 0;
        if (phase<1240) return -42;
        if (phase<1420) return -84;
        if (phase<1600) return -42;
        return 0;
    }
    public float groundAt(float screenX) { return terrain(distance+screenX); }
    public float slopeAt(float screenX) { return (groundAt(screenX+4)-groundAt(screenX-4))/8; }
    public float speed() { return START_SPEED+Math.min(distance/100,150); }
    public void tap() {
        if (over) reset();
        started=true;
        if (jumpsUsed < 2) { velocity=JUMP; gapTime=0; jumpsUsed++; }
    }
    public void step(float dt) {
        if (!started || over || dt<=0) return;
        dt=Math.min(dt, .035f);
        float oldGround=groundAt(RIDER_X);
        float movement=speed()*dt;
        distance+=movement; spawn-=movement;
        float rise=oldGround-groundAt(RIDER_X);
        // A vertical step must be jumped; descending ledges produce a fall.
        if(rise>20 && y>-rise+10){over=true;return;}
        // A grounded bicycle follows slopes. An airborne bicycle keeps its world height.
        if (y<0 || velocity<0 || rise < -20) {
            y+=rise;
            velocity+=GRAVITY*dt; y+=velocity*dt;
            if (y>=0) { y=0; velocity=0; }
        }
        if (spawn<=0) {
            boolean gap=random.nextBoolean();
            // Introduce double-jump challenges after the opening section.
            boolean tall = distance > 1000 && random.nextInt(4) == 0;
            float width = gap ? (tall ? speed() * .95f : 115 + random.nextInt(40)) : 36 + random.nextInt(22);
            float height = gap ? 0 : tall ? 165 + random.nextInt(16) : 28 + random.nextInt(27);
            boolean spikes=!gap && !tall && random.nextBoolean();
            if(spikes){width=60+random.nextInt(40);height=30;}
            // Give the rider a flat landing area and avoid placing a hazard at a step.
            float x=760;
            for(int i=0;i<40;i++){
                float g=groundAt(x),end=groundAt(x+width+120);
                if(Math.abs(g-end)<4 && Math.abs(groundAt(x+width/2)-g)<4)break;
                x+=20;
            }
            hazards.add(new Hazard(x, width, height, gap,spikes));
            // Leave room to land and recharge both jumps before the next obstacle.
            spawn=(x-760)+Math.max(440 + random.nextInt(220), width + speed() * 1.25f);
        }
        boolean unsupported=false;
        for (int n=hazards.size()-1; n>=0; n--) {
            Hazard h=hazards.get(n); h.x-=movement;
            if (h.gap) {
                // Inset cliff edges and allow a brief last-moment jump.
                if (RIDER_X>h.x+14 && RIDER_X<h.x+h.width-14 && y>=-6 && velocity>=0) unsupported=true;
            } else {
                float base=groundAt(h.x+h.width/2)-groundAt(RIDER_X);
                // Collision box is smaller than the visible bicycle and obstacle.
                if (RIDER_X+14>h.x+8 && RIDER_X-14<h.x+h.width-8 && y>base-h.height+10) over=true;
            }
            if (h.x+h.width<0) hazards.remove(n);
        }
        gapTime=unsupported?gapTime+dt:0;
        if (!unsupported && y==0 && velocity==0) jumpsUsed=0;
        if (gapTime>EDGE_GRACE) over=true;
    }
    public int score() { return (int)(distance/10); }
}
