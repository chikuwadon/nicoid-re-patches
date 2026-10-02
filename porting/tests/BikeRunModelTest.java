package e.e.a;
public final class BikeRunModelTest {
    private static void check(boolean ok,String message) { if(!ok)throw new AssertionError(message); }
    public static void main(String[] args) {
        BikeRunModel m=new BikeRunModel(7);
        m.step(.02f); check(m.distance==0,"waiting must not scroll");
        m.tap(); m.step(.02f); check(m.y<0,"tap must jump");
        for(int n=0;n<60;n++)m.step(.02f);
        check(m.y==0&&!m.over,"jump lands safely on clear ground");
        m.hazards.add(new BikeRunModel.Hazard(110,40,40,false)); m.step(.01f); check(m.over,"obstacle collision ends round");
        m.tap(); check(!m.over&&m.hazards.isEmpty(),"retry clears old hazards");
        m.y=-100; m.hazards.add(new BikeRunModel.Hazard(110,40,40,false)); m.step(.01f); check(!m.over,"airborne rider clears obstacle");
        m.reset(); m.started=true; m.hazards.add(new BikeRunModel.Hazard(110,140,0,true)); m.step(.01f); check(m.over,"grounded rider falls into gap");
        m.reset(); m.started=true; m.y=-100; m.hazards.add(new BikeRunModel.Hazard(110,140,0,true)); m.step(.01f); check(!m.over,"airborne rider crosses gap");
        float distance=m.distance; m.step(0); check(m.distance==distance,"paused step does not progress");
        System.out.println("Bicycle runner physics checks passed");
    }
}
