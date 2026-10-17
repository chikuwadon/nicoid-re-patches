package e.e.a;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

/** Keep native user pagination/actions while separating followed content into tabs. */
public final class Following190 {
 private Following190(){}
 public static void attach(Activity a){try{
  ListView users=(ListView)Review181.field(a,"A");
  ListAdapter nativeAdapter=(ListAdapter)Review181.field(a,"C");
  ListAdapter installed=users.getAdapter();
  if(installed instanceof HeaderViewListAdapter)installed=((HeaderViewListAdapter)installed).getWrappedAdapter();
  if(!(installed instanceof UserRows)||((UserRows)installed).original!=nativeAdapter)users.setAdapter(new UserRows(nativeAdapter));
  ViewGroup parent=(ViewGroup)users.getParent();if(parent==null||parent instanceof Pager)return;
  VideoDetails.initializeCookies(a);int index=parent.indexOfChild(users);ViewGroup.LayoutParams old=users.getLayoutParams();parent.removeView(users);
  LinearLayout page=PanelUi.column(a);page.setTag("nicoid_following_tabs");ThemeChoice.background(page);old.height=-1;parent.addView(page,index,old);
  LinearLayout tabs=new LinearLayout(a);page.addView(tabs,new LinearLayout.LayoutParams(-1,-2));
  Pager pager=new Pager(a);pager.addView(users,new FrameLayout.LayoutParams(-1,-1));
  LinearLayout[] rows=new LinearLayout[3];
  for(int i=1;i<3;i++){ScrollView scroll=new ScrollView(a);rows[i]=PanelUi.column(a);scroll.addView(rows[i]);pager.addView(scroll,new FrameLayout.LayoutParams(-1,-1));scroll.setVisibility(View.GONE);}
  TextView[] buttons=new TextView[3];View[] indicators=new View[3];NetworkTask[] loading={null};boolean[] visited={true,false,false};
  Runnable title=()->{String label=PanelUi.tr(a,"フォロー中","Following","追蹤中");a.setTitle(label);try{Object toolbar=Review181.field(a,"L");toolbar.getClass().getMethod("b",CharSequence.class).invoke(toolbar,label);}catch(Exception ignored){}};users.post(title);
  pager.changed=()->{int tab=pager.selected;title.run();for(int j=0;j<3;j++){indicators[j].setBackgroundColor(j==tab?UiDialogs.accent(a):android.graphics.Color.TRANSPARENT);buttons[j].setSelected(j==tab);buttons[j].setTextColor(j==tab?UiDialogs.accent(a):PanelUi.ink(a));}
   if(tab>0&&!visited[tab]){visited[tab]=true;if(tab==1){loading[0]=new NetworkTask();CreatorPage.loadFollowingChannels(a,rows[tab],loading[0]);}else CreatorPage.loadMylists(a,rows[tab],1);}
  };
  page.addView(pager,new LinearLayout.LayoutParams(-1,0,1));
  pager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(View v){}public void onViewDetachedFromWindow(View v){if(loading[0]!=null)loading[0].cancel();if(rows[2].getTag() instanceof NetworkTask)((NetworkTask)rows[2].getTag()).cancel();}});
  String[] ja={"ユーザー","チャンネル","マイリスト"},en={"Users","Channels","Mylists"},zh={"使用者","頻道","播放清單"};
  for(int i=0;i<3;i++){final int tab=i;LinearLayout slot=PanelUi.column(a);tabs.addView(slot,new LinearLayout.LayoutParams(0,-2,1));buttons[i]=PanelUi.text(a,PanelUi.tr(a,ja[i],en[i],zh[i]),14);buttons[i].setGravity(android.view.Gravity.CENTER);slot.addView(buttons[i],new LinearLayout.LayoutParams(-1,PanelUi.dp(a,48)));indicators[i]=new View(a);indicators[i].setBackgroundColor(i==0?UiDialogs.accent(a):android.graphics.Color.TRANSPARENT);slot.addView(indicators[i],new LinearLayout.LayoutParams(-1,PanelUi.dp(a,2)));buttons[i].setSelected(i==0);buttons[i].setTextColor(i==0?UiDialogs.accent(a):PanelUi.ink(a));buttons[i].setOnClickListener(v->pager.select(tab));}
 }catch(Exception error){android.util.Log.w("nicoid-following","Unable to initialize following tabs",error);}}
 /** Intercept only horizontal gestures; child lists retain vertical scrolling and long presses. */
 static final class Pager extends TabViewport110 {
  int selected;Runnable changed;boolean animating;
  Pager(android.content.Context c){super(c);}
  boolean switching(){return animating;}void swipe(int direction){select(selected+direction);}
  void select(int tab){if(tab<0||tab>=3||tab==selected||animating)return;int previous=selected;selected=tab;View outgoing=getChildAt(previous),incoming=getChildAt(tab);int direction=tab>previous?1:-1;incoming.setVisibility(View.VISIBLE);incoming.setTranslationX(direction*getWidth());if(changed!=null)changed.run();if(getWidth()==0){outgoing.setVisibility(View.GONE);incoming.setTranslationX(0);return;}animating=true;outgoing.animate().translationX(-direction*getWidth()).setDuration(200).start();incoming.animate().translationX(0).setDuration(200).withEndAction(()->{outgoing.setVisibility(View.GONE);outgoing.setTranslationX(0);animating=false;}).start();}
  protected void onDetachedFromWindow(){for(int i=0;i<getChildCount();i++){View v=getChildAt(i);v.animate().cancel();v.setTranslationX(0);v.setVisibility(i==selected?View.VISIBLE:View.GONE);}animating=false;super.onDetachedFromWindow();}
 }
 static final class Gestures {
 static boolean isHorizontal(float dx,float dy,int slop){return Math.abs(dx)>slop&&Math.abs(dx)>Math.abs(dy)*1.3f;}
 static boolean shouldSwitch(float dx,float velocity,int width,int fling){return width>0&&(Math.abs(dx)>width*.2f||(Math.abs(dx)>width*.04f&&Math.abs(velocity)>fling&&dx*velocity>0));}
 }
 static void sizeRow(View row){row.setMinimumHeight(PanelUi.dp(row.getContext(),64));row.setPadding(PanelUi.dp(row.getContext(),16),PanelUi.dp(row.getContext(),10),PanelUi.dp(row.getContext(),16),PanelUi.dp(row.getContext(),10));}
 static final class UserRows extends BaseAdapter {final ListAdapter original;UserRows(ListAdapter original){this.original=original;original.registerDataSetObserver(new android.database.DataSetObserver(){public void onChanged(){notifyDataSetChanged();}public void onInvalidated(){notifyDataSetInvalidated();}});}public int getCount(){return original.getCount();}public Object getItem(int p){return original.getItem(p);}public long getItemId(int p){return original.getItemId(p);}public boolean isEnabled(int p){return original.isEnabled(p);}public boolean areAllItemsEnabled(){return original.areAllItemsEnabled();}public int getViewTypeCount(){return original.getViewTypeCount();}public int getItemViewType(int p){return original.getItemViewType(p);}public View getView(int p,View convert,ViewGroup parent){View row=original.getView(p,convert,parent);sizeRow(row);ViewGroup.LayoutParams params=row.getLayoutParams();if(params!=null){params.height=-2;row.setLayoutParams(params);}if(row instanceof LinearLayout)((LinearLayout)row).setGravity(android.view.Gravity.CENTER_VERTICAL);style(row);return row;}}
 static void roundAvatar(View view){view.setOutlineProvider(new android.view.ViewOutlineProvider(){public void getOutline(View v,android.graphics.Outline outline){int size=Math.min(v.getWidth(),v.getHeight()),left=(v.getWidth()-size)/2,top=(v.getHeight()-size)/2;outline.setOval(left,top,left+size,top+size);}});view.setClipToOutline(true);}
 static void style(View view){if(view instanceof TextView){ViewGroup.LayoutParams textParams=view.getLayoutParams();if(textParams!=null){textParams.height=-2;if(textParams instanceof LinearLayout.LayoutParams){textParams.width=0;((LinearLayout.LayoutParams)textParams).weight=1;}view.setLayoutParams(textParams);}((TextView)view).setTextSize(16);((TextView)view).setTextColor(PanelUi.ink(view.getContext()));}if(view instanceof ImageView){ViewGroup.LayoutParams p=view.getLayoutParams();p.width=p.height=PanelUi.dp(view.getContext(),44);if(p instanceof ViewGroup.MarginLayoutParams)((ViewGroup.MarginLayoutParams)p).setMargins(0,0,0,0);view.setLayoutParams(p);((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_CROP);((ImageView)view).setAdjustViewBounds(false);roundAvatar(view);}if(view instanceof ViewGroup){ViewGroup g=(ViewGroup)view;for(int i=0;i<g.getChildCount();i++)style(g.getChildAt(i));}}
}
