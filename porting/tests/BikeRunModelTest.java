package e.e.a;
public final class BikeRunModelTest {
    private static void check(boolean ok,String message) { if(!ok)throw new AssertionError(message); }
    public static void main(String[] args) {
        BikeRunModel m=new BikeRunModel(7);
        m.step(.02f); check(m.distance==0,"waiting must not scroll");
        m.tap(); check(m.velocity<0,"touch begins jump immediately");
        m.step(.02f); check(m.y<0 && m.distance>=7,"jump starts at increased speed");
        for(int n=0;n<60;n++)m.step(.02f);
        check(m.y==0&&!m.over,"jump lands safely on clear ground");
        m.hazards.add(new BikeRunModel.Hazard(110,40,40,false)); m.step(.01f); check(m.over,"obstacle collision ends round");
        m.tap(); check(!m.over&&m.hazards.isEmpty(),"retry clears old hazards");
        m.y=-100; m.hazards.add(new BikeRunModel.Hazard(110,40,40,false)); m.step(.01f); check(!m.over,"airborne rider clears obstacle");
        m.reset(); m.started=true; m.hazards.add(new BikeRunModel.Hazard(145,40,40,false)); m.step(.01f);
        check(!m.over,"visible front wheel grazing obstacle has horizontal tolerance");
        m.reset(); m.started=true; m.y=-35; m.hazards.add(new BikeRunModel.Hazard(110,40,40,false)); m.step(.01f);
        check(!m.over,"grazing obstacle top has vertical tolerance");
        m.reset(); m.started=true; m.hazards.add(new BikeRunModel.Hazard(100,140,0,true)); m.step(.03f);
        check(!m.over,"cliff edge allows last-moment jump");
        m.tap(); m.step(.02f); check(m.y<0&&!m.over,"jump during edge grace saves rider");
        m.reset(); m.started=true; m.hazards.add(new BikeRunModel.Hazard(100,140,0,true));
        for(int n=0;n<4;n++)m.step(.03f);
        check(m.over,"remaining inside gap beyond grace ends round");
        m.reset(); m.started=true; m.y=-100; m.hazards.add(new BikeRunModel.Hazard(100,140,0,true)); m.step(.01f);
        check(!m.over,"airborne rider crosses gap");
        float distance=m.distance; m.step(0); check(m.distance==distance,"paused step does not progress");
        check(BikeRunModel.terrain(500)==0 && BikeRunModel.terrain(1100)<-60,"hill starts after flat opening");
        m.reset(); m.distance=630; check(m.slopeAt(120)<0,"uphill exists");
        m.distance=1130; check(m.slopeAt(120)>0,"downhill exists");
        m.reset(); m.started=true; m.spawn=100000;
        for(int n=0;n<260;n++) { m.step(.02f); check(m.y==0&&!m.over,"grounded rider follows hills without falling"); }
        m.distance=630; float old=m.groundAt(120); m.tap(); m.step(.02f);
        float worldY=m.y+m.groundAt(120);
        check(Math.abs(worldY-(old+(-650+1600*.02f)*.02f))<.01f,"hill jump keeps continuous world height");
        for(int n=0;n<70;n++)m.step(.02f);
        check(m.y==0&&!m.over,"hill jump lands on changing terrain");
        System.out.println("Bicycle runner physics checks passed");
    }
}
