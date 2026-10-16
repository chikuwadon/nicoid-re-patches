.class public final Le/e/a/PopupControls193;
.super Ljava/lang/Object;
.source "PopupControls193.java"

# interfaces
.implements Landroid/view/View$OnLayoutChangeListener;
.implements Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;
.implements Landroid/view/View$OnAttachStateChangeListener;


# static fields
.field static final TAG:Ljava/lang/String; = "popup-custom-controls"


# instance fields
.field final buttons:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation
.end field

.field final center:Landroid/view/View;

.field final comments:Landroid/view/View;

.field final controller:Landroid/view/View;

.field fittedHeight:I

.field fittedWidth:I

.field final menu:Landroid/widget/RelativeLayout;

.field final owner:Ljava/lang/Object;

.field final prefs:Landroid/content/SharedPreferences;

.field registered:Z

.field final root:Landroid/view/View;

.field final row:Landroid/widget/LinearLayout;

.field final stage:Landroid/widget/RelativeLayout;

.field updating:Z

.field final video:Landroid/view/View;


# direct methods
.method constructor <init>(Ljava/lang/Object;Landroid/view/View;Landroid/widget/RelativeLayout;Landroid/widget/RelativeLayout;)V
    .registers 12

    .line 35
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 22
    new-instance v0, Ljava/util/LinkedHashMap;

    invoke-direct {v0}, Ljava/util/LinkedHashMap;-><init>()V

    iput-object v0, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    .line 36
    iput-object p1, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    iput-object p2, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    iput-object p3, p0, Le/e/a/PopupControls193;->stage:Landroid/widget/RelativeLayout;

    iput-object p4, p0, Le/e/a/PopupControls193;->menu:Landroid/widget/RelativeLayout;

    .line 37
    invoke-virtual {p2}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    invoke-static {v0}, Landroid/preference/PreferenceManager;->getDefaultSharedPreferences(Landroid/content/Context;)Landroid/content/SharedPreferences;

    move-result-object v0

    iput-object v0, p0, Le/e/a/PopupControls193;->prefs:Landroid/content/SharedPreferences;

    .line 38
    const-string v0, "video"

    invoke-static {p2, v0}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Le/e/a/PopupControls193;->video:Landroid/view/View;

    const-string v0, "commentlay"

    invoke-static {p2, v0}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Le/e/a/PopupControls193;->comments:Landroid/view/View;

    const-string v0, "viewctrllay"

    invoke-static {p2, v0}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    const-string v0, "controller"

    invoke-static {p2, v0}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v0

    iput-object v0, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    .line 39
    const-string v0, "popup-modern-controls"

    invoke-virtual {p2, v0}, Landroid/view/View;->findViewWithTag(Ljava/lang/Object;)Landroid/view/View;

    move-result-object v0

    .line 40
    instance-of v1, v0, Landroid/view/ViewGroup;

    const/4 v2, 0x0

    const/4 v3, 0x1

    if-eqz v1, :cond_74

    move-object v1, v0

    check-cast v1, Landroid/view/ViewGroup;

    .line 41
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getChildCount()I

    move-result v4

    const/4 v5, 0x3

    if-lt v4, v5, :cond_74

    iget-object v4, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v5, "speed"

    invoke-virtual {v1, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object v6

    invoke-interface {v4, v5, v6}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    iget-object v4, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v5, "quality"

    invoke-virtual {v1, v3}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object v6

    invoke-interface {v4, v5, v6}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    iget-object v4, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const/4 v5, 0x2

    invoke-virtual {v1, v5}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object v1

    const-string v5, "repeat"

    invoke-interface {v4, v5, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 42
    :cond_74
    iget-object v1, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v4, "commentbutton"

    invoke-static {p2, v4}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v4

    const-string v5, "comments"

    invoke-interface {v1, v5, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    iget-object v1, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v4, "fullscbutton"

    invoke-static {p2, v4}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v4

    const-string v5, "fullscreen"

    invoke-interface {v1, v5, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 43
    iget-object v1, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v4, "infobutton"

    invoke-static {p2, v4}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v4

    const-string v5, "info"

    invoke-interface {v1, v5, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    iget-object v1, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v4, "popup-close"

    invoke-virtual {p2, v4}, Landroid/view/View;->findViewWithTag(Ljava/lang/Object;)Landroid/view/View;

    move-result-object v4

    const-string v5, "close"

    invoke-interface {v1, v5, v4}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 44
    iget-object v1, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    invoke-interface {v1}, Ljava/util/Map;->values()Ljava/util/Collection;

    move-result-object v1

    invoke-interface {v1}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    move-result-object v1

    :goto_b2
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-nez v4, :cond_14e

    .line 45
    invoke-static {v0}, Le/e/a/PopupControls193;->detach(Landroid/view/View;)V

    .line 46
    const-string v0, "popup-controls-surface"

    invoke-virtual {p2, v0}, Landroid/view/View;->findViewWithTag(Ljava/lang/Object;)Landroid/view/View;

    move-result-object v0

    invoke-static {v0}, Le/e/a/PopupControls193;->detach(Landroid/view/View;)V

    .line 47
    iget-object v0, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    new-instance v1, Le/e/a/PopupControls193$$ExternalSyntheticLambda0;

    invoke-direct {v1, p0}, Le/e/a/PopupControls193$$ExternalSyntheticLambda0;-><init>(Le/e/a/PopupControls193;)V

    const-string v4, "headphones"

    const-string v5, "\u30d0\u30c3\u30af\u30b0\u30e9\u30a6\u30f3\u30c9\u518d\u751f"

    invoke-virtual {p0, v4, v5, v1}, Le/e/a/PopupControls193;->newButton(Ljava/lang/String;Ljava/lang/String;Landroid/view/View$OnClickListener;)Landroid/view/View;

    move-result-object v1

    const-string v4, "background"

    invoke-interface {v0, v4, v1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 48
    new-array v0, v3, [F

    const/high16 v1, 0x3f800000    # 1.0f

    aput v1, v0, v2

    .line 49
    new-instance v1, Le/e/a/PopupControls193$$ExternalSyntheticLambda1;

    invoke-direct {v1, p1, v0}, Le/e/a/PopupControls193$$ExternalSyntheticLambda1;-><init>(Ljava/lang/Object;[F)V

    const-string p1, "volume"

    const-string v0, "\u30df\u30e5\u30fc\u30c8"

    invoke-virtual {p0, p1, v0, v1}, Le/e/a/PopupControls193;->newButton(Ljava/lang/String;Ljava/lang/String;Landroid/view/View$OnClickListener;)Landroid/view/View;

    move-result-object p1

    .line 55
    iget-object v0, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v1, "mute"

    invoke-interface {v0, v1, p1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 56
    new-instance p1, Landroid/widget/LinearLayout;

    invoke-virtual {p2}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    invoke-direct {p1, v0}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    iput-object p1, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    iget-object p1, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    const-string v0, "popup-custom-controls"

    invoke-virtual {p1, v0}, Landroid/widget/LinearLayout;->setTag(Ljava/lang/Object;)V

    iget-object p1, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    const/16 v0, 0x15

    invoke-virtual {p1, v0}, Landroid/widget/LinearLayout;->setGravity(I)V

    .line 57
    new-instance p1, Landroid/widget/RelativeLayout$LayoutParams;

    const/4 v0, -0x1

    invoke-direct {p1, v0, v3}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    const/16 v1, 0xa

    invoke-virtual {p1, v1}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    .line 58
    iget-object v1, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-virtual {p4, v1, p1}, Landroid/widget/RelativeLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 60
    iget-object p1, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    if-eqz p1, :cond_135

    iget-object p1, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    invoke-static {p1}, Le/e/a/PopupControls193;->detach(Landroid/view/View;)V

    iget-object p1, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    const/4 p4, 0x0

    invoke-virtual {p1, p4}, Landroid/view/View;->setTranslationY(F)V

    iget-object p1, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    new-instance p4, Landroid/widget/RelativeLayout$LayoutParams;

    const/4 v1, -0x2

    invoke-direct {p4, v0, v1}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    invoke-virtual {p3, p1, p4}, Landroid/widget/RelativeLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 61
    :cond_135
    invoke-virtual {p2, p0}, Landroid/view/View;->addOnLayoutChangeListener(Landroid/view/View$OnLayoutChangeListener;)V

    invoke-virtual {p3, p0}, Landroid/widget/RelativeLayout;->addOnLayoutChangeListener(Landroid/view/View$OnLayoutChangeListener;)V

    .line 62
    iget-object p1, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    if-eqz p1, :cond_144

    iget-object p1, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    invoke-virtual {p1, p0}, Landroid/view/View;->addOnLayoutChangeListener(Landroid/view/View$OnLayoutChangeListener;)V

    .line 63
    :cond_144
    invoke-virtual {p2, p0}, Landroid/view/View;->addOnAttachStateChangeListener(Landroid/view/View$OnAttachStateChangeListener;)V

    invoke-virtual {p0}, Le/e/a/PopupControls193;->register()V

    invoke-virtual {p0}, Le/e/a/PopupControls193;->update()V

    .line 64
    return-void

    .line 44
    :cond_14e
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Landroid/view/View;

    invoke-static {v4}, Le/e/a/PopupControls193;->detach(Landroid/view/View;)V

    goto/16 :goto_b2
.end method

.method public static attach(Ljava/lang/Object;Landroid/view/View;)V
    .registers 5

    .line 30
    instance-of v0, p0, Landroid/app/Service;

    if-eqz v0, :cond_2b

    const-string v0, "popup-custom-controls"

    invoke-virtual {p1, v0}, Landroid/view/View;->findViewWithTag(Ljava/lang/Object;)Landroid/view/View;

    move-result-object v0

    if-eqz v0, :cond_d

    goto :goto_2b

    .line 31
    :cond_d
    const-string v0, "videoLayout"

    invoke-static {p1, v0}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v0

    const-string v1, "topmenulay"

    invoke-static {p1, v1}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v1

    .line 32
    instance-of v2, v0, Landroid/widget/RelativeLayout;

    if-eqz v2, :cond_2a

    instance-of v2, v1, Landroid/widget/RelativeLayout;

    if-eqz v2, :cond_2a

    .line 33
    new-instance v2, Le/e/a/PopupControls193;

    check-cast v0, Landroid/widget/RelativeLayout;

    check-cast v1, Landroid/widget/RelativeLayout;

    invoke-direct {v2, p0, p1, v0, v1}, Le/e/a/PopupControls193;-><init>(Ljava/lang/Object;Landroid/view/View;Landroid/widget/RelativeLayout;Landroid/widget/RelativeLayout;)V

    .line 34
    :cond_2a
    return-void

    .line 30
    :cond_2b
    :goto_2b
    return-void
.end method

.method static binding(Landroid/view/View;)Le/e/a/PopupControls193;
    .registers 2

    .line 109
    const-string v0, "popup-custom-controls"

    invoke-virtual {p0, v0}, Landroid/view/View;->findViewWithTag(Ljava/lang/Object;)Landroid/view/View;

    move-result-object p0

    if-eqz p0, :cond_1e

    invoke-virtual {p0}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    instance-of v0, v0, Landroid/view/View;

    if-eqz v0, :cond_1e

    const v0, 0x7f0801cf

    invoke-virtual {p0, v0}, Landroid/view/View;->getTag(I)Ljava/lang/Object;

    move-result-object p0

    instance-of v0, p0, Le/e/a/PopupControls193;

    if-eqz v0, :cond_1e

    check-cast p0, Le/e/a/PopupControls193;

    return-object p0

    :cond_1e
    const/4 p0, 0x0

    return-object p0
.end method

.method static detach(Landroid/view/View;)V
    .registers 2

    .line 66
    if-eqz p0, :cond_13

    invoke-virtual {p0}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    instance-of v0, v0, Landroid/view/ViewGroup;

    if-eqz v0, :cond_13

    invoke-virtual {p0}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    move-result-object v0

    check-cast v0, Landroid/view/ViewGroup;

    invoke-virtual {v0, p0}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    :cond_13
    return-void
.end method

.method static find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;
    .registers 5

    .line 27
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    invoke-virtual {p0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v1

    invoke-virtual {v1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v1

    const-string v2, "id"

    invoke-virtual {v0, p1, v2, v1}, Landroid/content/res/Resources;->getIdentifier(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I

    move-result p1

    invoke-virtual {p0, p1}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p0

    return-object p0
.end method

.method static synthetic lambda$1(Ljava/lang/Object;[FLandroid/view/View;)V
    .registers 10

    .line 50
    :try_start_0
    const-string v0, "e"

    invoke-static {p0, v0}, Le/e/a/Review181;->field(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;

    move-result-object p0

    invoke-virtual {p2}, Landroid/view/View;->isSelected()Z

    move-result v0
    :try_end_a
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_a} :catch_5a

    .line 51
    const/4 v1, 0x1

    xor-int/2addr v0, v1

    const/4 v2, 0x0

    if-eqz v0, :cond_2b

    :try_start_f
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    const-string v4, "getVolume"

    new-array v5, v2, [Ljava/lang/Class;

    invoke-virtual {v3, v4, v5}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v3

    new-array v4, v2, [Ljava/lang/Object;

    invoke-virtual {v3, p0, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Ljava/lang/Number;

    invoke-virtual {v3}, Ljava/lang/Number;->floatValue()F

    move-result v3

    aput v3, p1, v2
    :try_end_29
    .catch Ljava/lang/Exception; {:try_start_f .. :try_end_29} :catch_2a

    goto :goto_2b

    :catch_2a
    move-exception v3

    .line 52
    :cond_2b
    :goto_2b
    :try_start_2b
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    const-string v4, "setVolume"

    new-array v5, v1, [Ljava/lang/Class;

    sget-object v6, Ljava/lang/Float;->TYPE:Ljava/lang/Class;

    aput-object v6, v5, v2

    invoke-virtual {v3, v4, v5}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v3

    new-array v1, v1, [Ljava/lang/Object;

    if-eqz v0, :cond_41

    const/4 p1, 0x0

    goto :goto_43

    :cond_41
    aget p1, p1, v2

    :goto_43
    invoke-static {p1}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object p1

    aput-object p1, v1, v2

    invoke-virtual {v3, p0, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    .line 53
    invoke-virtual {p2, v0}, Landroid/view/View;->setSelected(Z)V

    if-eqz v0, :cond_54

    const-string p0, "muted"

    goto :goto_56

    :cond_54
    const-string p0, "volume"

    :goto_56
    invoke-static {p2, p0}, Le/e/a/PlayerIcons;->symbol(Landroid/view/View;Ljava/lang/String;)V
    :try_end_59
    .catch Ljava/lang/Exception; {:try_start_2b .. :try_end_59} :catch_5a

    .line 54
    goto :goto_62

    :catch_5a
    move-exception p0

    const-string p1, "nicoid-popup"

    const-string p2, "Mute failed"

    invoke-static {p1, p2, p0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    :goto_62
    return-void
.end method

.method static size(Landroid/view/View;II)V
    .registers 5

    .line 76
    if-nez p0, :cond_3

    return-void

    :cond_3
    invoke-virtual {p0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    move-result-object v0

    if-nez v0, :cond_e

    new-instance v0, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v0, p1, p2}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    :cond_e
    iget v1, v0, Landroid/view/ViewGroup$LayoutParams;->width:I

    if-ne v1, p1, :cond_16

    iget v1, v0, Landroid/view/ViewGroup$LayoutParams;->height:I

    if-eq v1, p2, :cond_1d

    :cond_16
    iput p1, v0, Landroid/view/ViewGroup$LayoutParams;->width:I

    iput p2, v0, Landroid/view/ViewGroup$LayoutParams;->height:I

    invoke-virtual {p0, v0}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    :cond_1d
    const/4 p1, 0x0

    invoke-virtual {p0, p1}, Landroid/view/View;->setMinimumWidth(I)V

    invoke-virtual {p0, p1}, Landroid/view/View;->setMinimumHeight(I)V

    invoke-virtual {p0, p1, p1, p1, p1}, Landroid/view/View;->setPadding(IIII)V

    instance-of v0, p0, Landroid/widget/TextView;

    if-eqz v0, :cond_44

    check-cast p0, Landroid/widget/TextView;

    invoke-virtual {p0, p1}, Landroid/widget/TextView;->setMinWidth(I)V

    invoke-virtual {p0, p1}, Landroid/widget/TextView;->setMinHeight(I)V

    invoke-virtual {p0, p1}, Landroid/widget/TextView;->setIncludeFontPadding(Z)V

    const/16 v0, 0x11

    invoke-virtual {p0, v0}, Landroid/widget/TextView;->setGravity(I)V

    int-to-float p2, p2

    const v0, 0x3eae147b    # 0.34f

    mul-float p2, p2, v0

    invoke-virtual {p0, p1, p2}, Landroid/widget/TextView;->setTextSize(IF)V

    :cond_44
    return-void
.end method


# virtual methods
.method background()V
    .registers 7

    .line 67
    const-string v0, "b"

    const/4 v1, 0x0

    :try_start_3
    iget-object v2, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    invoke-static {v2, v0}, Le/e/a/Review181;->field(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v2

    instance-of v3, v2, Landroid/view/WindowManager;

    if-eqz v3, :cond_5e

    check-cast v2, Landroid/view/WindowManager;

    iget-object v3, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-interface {v2, v3}, Landroid/view/WindowManager;->removeView(Landroid/view/View;)V

    iget-object v2, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    invoke-virtual {v2}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v2

    const-string v3, "p0"

    invoke-virtual {v2, v3}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v2

    const/4 v3, 0x0

    const/4 v4, 0x1

    invoke-virtual {v2, v3, v4}, Ljava/lang/reflect/Field;->setBoolean(Ljava/lang/Object;Z)V

    iget-object v2, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    invoke-virtual {v2}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v2

    new-array v3, v4, [Ljava/lang/Class;

    sget-object v5, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    aput-object v5, v3, v1

    invoke-virtual {v2, v0, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    iget-object v2, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    new-array v3, v4, [Ljava/lang/Object;

    invoke-static {v4}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    move-result-object v4

    aput-object v4, v3, v1

    invoke-virtual {v0, v2, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_42
    .catch Ljava/lang/Exception; {:try_start_3 .. :try_end_42} :catch_43

    goto :goto_5e

    :catch_43
    move-exception v0

    const-string v2, "nicoid-popup"

    const-string v3, "Background switch failed"

    invoke-static {v2, v3, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    iget-object v0, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-virtual {v0}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v0

    const-string v2, "\u30d0\u30c3\u30af\u30b0\u30e9\u30a6\u30f3\u30c9\u518d\u751f\u306b\u5207\u308a\u66ff\u3048\u3089\u308c\u307e\u305b\u3093\u3067\u3057\u305f"

    invoke-static {v2}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v0, v2, v1}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v0

    invoke-virtual {v0}, Landroid/widget/Toast;->show()V

    :cond_5e
    :goto_5e
    return-void
.end method

.method fitComments(Landroid/view/View;II)V
    .registers 11

    .line 108
    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v0

    const-string v1, "e.e.a.t"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    const/4 v2, 0x0

    if-nez v1, :cond_19

    const-string v1, "e.e.a.u"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_4e

    :cond_19
    invoke-static {p1, p2, p3}, Le/e/a/PopupControls193;->size(Landroid/view/View;II)V

    :try_start_1c
    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v0

    const-string v1, "a"

    const/4 v3, 0x2

    new-array v4, v3, [Ljava/lang/Class;

    sget-object v5, Ljava/lang/Float;->TYPE:Ljava/lang/Class;

    aput-object v5, v4, v2

    sget-object v5, Ljava/lang/Float;->TYPE:Ljava/lang/Class;

    const/4 v6, 0x1

    aput-object v5, v4, v6

    invoke-virtual {v0, v1, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    new-array v1, v3, [Ljava/lang/Object;

    int-to-float v3, p2

    invoke-static {v3}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v3

    aput-object v3, v1, v2

    int-to-float v3, p3

    invoke-static {v3}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v3

    aput-object v3, v1, v6

    invoke-virtual {v0, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_45
    .catch Ljava/lang/Exception; {:try_start_1c .. :try_end_45} :catch_46

    goto :goto_4e

    :catch_46
    move-exception v0

    const-string v1, "nicoid-popup"

    const-string v3, "Comment viewport update failed"

    invoke-static {v1, v3, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    :cond_4e
    :goto_4e
    instance-of v0, p1, Landroid/view/ViewGroup;

    if-eqz v0, :cond_65

    check-cast p1, Landroid/view/ViewGroup;

    :goto_54
    invoke-virtual {p1}, Landroid/view/ViewGroup;->getChildCount()I

    move-result v0

    if-lt v2, v0, :cond_5b

    goto :goto_65

    :cond_5b
    invoke-virtual {p1, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    move-result-object v0

    invoke-virtual {p0, v0, p2, p3}, Le/e/a/PopupControls193;->fitComments(Landroid/view/View;II)V

    add-int/lit8 v2, v2, 0x1

    goto :goto_54

    :cond_65
    :goto_65
    return-void
.end method

.method synthetic lambda$0$e-e-a-PopupControls193(Landroid/view/View;)V
    .registers 2

    .line 47
    invoke-virtual {p0}, Le/e/a/PopupControls193;->background()V

    return-void
.end method

.method synthetic lambda$2$e-e-a-PopupControls193()V
    .registers 1

    .line 111
    invoke-virtual {p0}, Le/e/a/PopupControls193;->update()V

    return-void
.end method

.method layout()V
    .registers 14

    .line 78
    iget-boolean v0, p0, Le/e/a/PopupControls193;->updating:Z

    if-eqz v0, :cond_5

    return-void

    :cond_5
    iget-object v0, p0, Le/e/a/PopupControls193;->stage:Landroid/widget/RelativeLayout;

    invoke-virtual {v0}, Landroid/widget/RelativeLayout;->getWidth()I

    move-result v0

    if-gtz v0, :cond_1f

    :try_start_d
    iget-object v1, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    const-string v2, "e"

    invoke-static {v1, v2}, Le/e/a/Review181;->field(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/view/View;

    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    move-result-object v1

    iget v0, v1, Landroid/view/ViewGroup$LayoutParams;->width:I
    :try_end_1d
    .catch Ljava/lang/Exception; {:try_start_d .. :try_end_1d} :catch_1e

    goto :goto_1f

    :catch_1e
    move-exception v1

    :cond_1f
    :goto_1f
    if-gtz v0, :cond_22

    return-void

    .line 79
    :cond_22
    const/4 v1, 0x1

    iput-boolean v1, p0, Le/e/a/PopupControls193;->updating:Z

    .line 80
    const/4 v2, 0x0

    :try_start_26
    iget-object v3, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-virtual {v3}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v3

    invoke-static {v3, v1}, Le/e/a/PlayerVisibility192;->enabled(Landroid/content/Context;Z)Z

    move-result v3

    .line 81
    iget-object v4, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-static {v4}, Le/e/a/Revision196;->popupCount(Landroid/widget/LinearLayout;)I

    move-result v4

    invoke-static {v1, v4}, Ljava/lang/Math;->max(II)I

    move-result v4

    .line 82
    iget-object v5, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-virtual {v5}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v5

    const/16 v6, 0x28

    invoke-static {v5, v6}, Le/e/a/PanelUi;->dp(Landroid/content/Context;I)I

    move-result v5

    div-int v4, v0, v4

    invoke-static {v5, v4}, Ljava/lang/Math;->min(II)I

    move-result v4

    invoke-static {v1, v4}, Ljava/lang/Math;->max(II)I

    move-result v4

    .line 83
    nop

    .line 84
    iget-object v5, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    const/4 v6, -0x1

    invoke-static {v5, v6, v4}, Le/e/a/PopupControls193;->size(Landroid/view/View;II)V
    invoke-static {v5, v4}, Le/e/a/Revision196;->popupInfoSize(Landroid/widget/LinearLayout;I)V
    const/high16 v7, -0x56000000
    invoke-virtual {v5, v7}, Landroid/view/View;->setBackgroundColor(I)V

    iget-object v5, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    const/16 v7, 0x15

    invoke-virtual {v5, v7}, Landroid/widget/LinearLayout;->setGravity(I)V

    .line 85
    const/4 v5, 0x0

    :goto_5f
    iget-object v7, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-virtual {v7}, Landroid/widget/LinearLayout;->getChildCount()I

    move-result v7

    if-lt v5, v7, :cond_1e2

    .line 86
    int-to-float v5, v0

    const/high16 v7, 0x41100000    # 9.0f

    mul-float v5, v5, v7

    const/high16 v7, 0x41800000    # 16.0f

    div-float/2addr v5, v7

    invoke-static {v5}, Ljava/lang/Math;->round(F)I

    move-result v5

    invoke-static {v1, v5}, Ljava/lang/Math;->max(II)I

    move-result v5

    .line 87
    nop

    .line 88
    iget-object v7, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    if-eqz v7, :cond_96

    iget-object v7, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    const/high16 v8, 0x40000000    # 2.0f

    invoke-static {v0, v8}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    move-result v8

    invoke-static {v2, v2}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    move-result v9

    invoke-virtual {v7, v8, v9}, Landroid/view/View;->measure(II)V

    iget-object v7, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    invoke-virtual {v7}, Landroid/view/View;->getMeasuredHeight()I

    move-result v7

    invoke-static {v1, v7}, Ljava/lang/Math;->max(II)I

    move-result v7

    goto :goto_97

    :cond_96
    const/4 v7, 0x0

    .line 89
    :goto_97
    if-eqz v3, :cond_9b

    move v8, v4

    goto :goto_9c

    :cond_9b
    const/4 v8, 0x0

    :goto_9c
    add-int v9, v5, v8

    if-eqz v3, :cond_a2

    move v10, v7

    goto :goto_a3

    :cond_a2
    const/4 v10, 0x0

    :goto_a3
    add-int/2addr v10, v9

    .line 90
    iget-object v11, p0, Le/e/a/PopupControls193;->stage:Landroid/widget/RelativeLayout;

    invoke-virtual {v11}, Landroid/widget/RelativeLayout;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    move-result-object v11

    iget v12, v11, Landroid/view/ViewGroup$LayoutParams;->height:I

    if-eq v12, v10, :cond_b5

    iput v10, v11, Landroid/view/ViewGroup$LayoutParams;->height:I

    iget-object v12, p0, Le/e/a/PopupControls193;->stage:Landroid/widget/RelativeLayout;

    invoke-virtual {v12, v11}, Landroid/widget/RelativeLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 91
    :cond_b5
    iget-object v11, p0, Le/e/a/PopupControls193;->video:Landroid/view/View;

    invoke-virtual {p0, v11, v6, v5, v8}, Le/e/a/PopupControls193;->rectangle(Landroid/view/View;III)V

    iget-object v11, p0, Le/e/a/PopupControls193;->comments:Landroid/view/View;

    invoke-virtual {p0, v11, v6, v5, v8}, Le/e/a/PopupControls193;->rectangle(Landroid/view/View;III)V

    .line 92
    iget-object v11, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    const-string v12, "video_view"

    invoke-static {v11, v12}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v11

    if-eqz v11, :cond_cc

    invoke-static {v11, v0, v5}, Le/e/a/PopupControls193;->size(Landroid/view/View;II)V

    .line 94
    :cond_cc
    iget-object v11, p0, Le/e/a/PopupControls193;->menu:Landroid/widget/RelativeLayout;

    if-eqz v3, :cond_d1

    goto :goto_d2

    :cond_d1
    move v4, v5

    :goto_d2
    invoke-virtual {p0, v11, v6, v4, v2}, Le/e/a/PopupControls193;->rectangle(Landroid/view/View;III)V

    iget-object v4, p0, Le/e/a/PopupControls193;->menu:Landroid/widget/RelativeLayout;

    invoke-virtual {v4, v2, v2, v2, v2}, Landroid/widget/RelativeLayout;->setPadding(IIII)V

    iget-object v4, p0, Le/e/a/PopupControls193;->menu:Landroid/widget/RelativeLayout;

    const/high16 v11, -0x1000000

    if-eqz v3, :cond_e3

    const/high16 v12, -0x1000000

    goto :goto_e4

    :cond_e3
    const/4 v12, 0x0

    :goto_e4
    invoke-virtual {v4, v12}, Landroid/widget/RelativeLayout;->setBackgroundColor(I)V

    .line 95
    iget-object v4, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    if-eqz v3, :cond_ec

    goto :goto_f2

    :cond_ec
    sub-int v9, v5, v7

    invoke-static {v2, v9}, Ljava/lang/Math;->max(II)I

    move-result v9

    :goto_f2
    invoke-virtual {p0, v4, v6, v7, v9}, Le/e/a/PopupControls193;->rectangle(Landroid/view/View;III)V

    .line 96
    iget-object v4, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    if-eqz v4, :cond_103

    iget-object v4, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    if-eqz v3, :cond_fe

    goto :goto_100

    :cond_fe
    const/high16 v11, -0x56000000

    :goto_100
    invoke-virtual {v4, v11}, Landroid/view/View;->setBackgroundColor(I)V

    .line 97
    :cond_103
    iget-object v4, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    const-string v7, "viewbutton"

    invoke-static {v4, v7}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v4

    iget-object v7, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-virtual {v7}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v7

    const/16 v9, 0x38

    invoke-static {v7, v9}, Le/e/a/PanelUi;->dp(Landroid/content/Context;I)I

    move-result v7

    div-int/lit8 v9, v5, 0x3

    invoke-static {v1, v9}, Ljava/lang/Math;->max(II)I

    move-result v9

    invoke-static {v7, v9}, Ljava/lang/Math;->min(II)I

    move-result v7

    invoke-static {v4, v7, v7}, Le/e/a/PopupControls193;->size(Landroid/view/View;II)V

    .line 98
    const/4 v4, 0x2

    new-array v9, v4, [Ljava/lang/String;

    const-string v11, "prevbutton"

    aput-object v11, v9, v2

    const-string v11, "nextbutton"

    aput-object v11, v9, v1

    const/4 v1, 0x0

    :goto_130
    if-lt v1, v4, :cond_1d3

    .line 99
    iget-object v1, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    sub-int v9, v5, v7

    div-int/2addr v9, v4

    add-int/2addr v9, v8

    invoke-virtual {p0, v1, v6, v7, v9}, Le/e/a/PopupControls193;->rectangle(Landroid/view/View;III)V

    .line 101
    iget-object v1, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    const-string v6, "videopro"

    invoke-static {v1, v6}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v1

    if-eqz v1, :cond_170

    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    move-result-object v6

    check-cast v6, Landroid/widget/RelativeLayout$LayoutParams;

    invoke-virtual {v1}, Landroid/view/View;->getMeasuredHeight()I

    move-result v7

    sub-int v7, v5, v7

    div-int/2addr v7, v4

    invoke-static {v2, v7}, Ljava/lang/Math;->max(II)I

    move-result v4

    add-int/2addr v8, v4

    iget v4, v6, Landroid/widget/RelativeLayout$LayoutParams;->topMargin:I

    const/16 v7, 0xf

    if-ne v4, v8, :cond_163

    invoke-virtual {v6, v7}, Landroid/widget/RelativeLayout$LayoutParams;->getRule(I)I

    move-result v4

    if-eqz v4, :cond_170

    :cond_163
    invoke-virtual {v6, v7}, Landroid/widget/RelativeLayout$LayoutParams;->removeRule(I)V

    iput v8, v6, Landroid/widget/RelativeLayout$LayoutParams;->topMargin:I

    const/16 v4, 0xa

    invoke-virtual {v6, v4}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    invoke-virtual {v1, v6}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 102
    :cond_170
    iget-object v1, p0, Le/e/a/PopupControls193;->comments:Landroid/view/View;

    if-eqz v1, :cond_185

    iget v1, p0, Le/e/a/PopupControls193;->fittedWidth:I

    if-ne v1, v0, :cond_17c

    iget v1, p0, Le/e/a/PopupControls193;->fittedHeight:I

    if-eq v1, v5, :cond_185

    :cond_17c
    iput v0, p0, Le/e/a/PopupControls193;->fittedWidth:I

    iput v5, p0, Le/e/a/PopupControls193;->fittedHeight:I

    iget-object v1, p0, Le/e/a/PopupControls193;->comments:Landroid/view/View;

    invoke-virtual {p0, v1, v0, v5}, Le/e/a/PopupControls193;->fitComments(Landroid/view/View;II)V
    :try_end_185
    .catchall {:try_start_26 .. :try_end_185} :catchall_1ef

    .line 103
    :cond_185
    :try_start_185
    iget-object v0, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    const-string v1, "U"

    invoke-static {v0, v1}, Le/e/a/Review181;->field(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/view/WindowManager$LayoutParams;

    .line 104
    iget v1, v0, Landroid/view/WindowManager$LayoutParams;->height:I

    if-eq v1, v10, :cond_1b6

    iput v10, v0, Landroid/view/WindowManager$LayoutParams;->height:I

    iget-object v1, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-virtual {v1}, Landroid/view/View;->isAttachedToWindow()Z

    move-result v1

    if-eqz v1, :cond_1b6

    iget-object v1, p0, Le/e/a/PopupControls193;->owner:Ljava/lang/Object;

    const-string v4, "b"

    invoke-static {v1, v4}, Le/e/a/Review181;->field(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/view/WindowManager;

    iget-object v4, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-interface {v1, v4, v0}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    :try_end_1ac
    .catch Ljava/lang/Exception; {:try_start_185 .. :try_end_1ac} :catch_1ad
    .catchall {:try_start_185 .. :try_end_1ac} :catchall_1ef

    goto :goto_1b5

    .line 105
    :catch_1ad
    move-exception v0

    :try_start_1ae
    const-string v1, "nicoid-popup"

    const-string v4, "Popup bounds update failed"

    invoke-static {v1, v4, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    :goto_1b5
    nop

    .line 106
    :cond_1b6
    if-eqz v3, :cond_1d0

    iget-object v0, p0, Le/e/a/PopupControls193;->menu:Landroid/widget/RelativeLayout;

    invoke-virtual {v0, v2}, Landroid/widget/RelativeLayout;->setVisibility(I)V

    iget-object v0, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    if-eqz v0, :cond_1c6

    iget-object v0, p0, Le/e/a/PopupControls193;->controller:Landroid/view/View;

    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    :cond_1c6
    iget-object v0, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    if-eqz v0, :cond_1d0

    iget-object v0, p0, Le/e/a/PopupControls193;->center:Landroid/view/View;

    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V
    :try_end_1cf
    .catchall {:try_start_1ae .. :try_end_1cf} :catchall_1ef

    .line 107
    nop

    :cond_1d0
    iput-boolean v2, p0, Le/e/a/PopupControls193;->updating:Z

    return-void

    .line 98
    :cond_1d3
    :try_start_1d3
    aget-object v11, v9, v1

    iget-object v12, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-static {v12, v11}, Le/e/a/PopupControls193;->find(Landroid/view/View;Ljava/lang/String;)Landroid/view/View;

    move-result-object v11

    invoke-static {v11, v7, v7}, Le/e/a/PopupControls193;->size(Landroid/view/View;II)V

    add-int/lit8 v1, v1, 0x1

    goto/16 :goto_130

    .line 85
    :cond_1e2
    iget-object v7, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-virtual {v7, v5}, Landroid/widget/LinearLayout;->getChildAt(I)Landroid/view/View;

    move-result-object v7

    invoke-static {v7, v4, v4}, Le/e/a/PopupControls193;->size(Landroid/view/View;II)V
    :try_end_1eb
    .catchall {:try_start_1d3 .. :try_end_1eb} :catchall_1ef

    add-int/lit8 v5, v5, 0x1

    goto/16 :goto_5f

    .line 107
    :catchall_1ef
    move-exception v0

    iput-boolean v2, p0, Le/e/a/PopupControls193;->updating:Z

    throw v0
.end method

.method newButton(Ljava/lang/String;Ljava/lang/String;Landroid/view/View$OnClickListener;)Landroid/view/View;
    .registers 6

    .line 65
    new-instance v0, Landroid/view/View;

    iget-object v1, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    invoke-virtual {v1}, Landroid/view/View;->getContext()Landroid/content/Context;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    invoke-static {v0, p1}, Le/e/a/PlayerIcons;->symbol(Landroid/view/View;Ljava/lang/String;)V

    invoke-static {p2}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    invoke-virtual {v0, p1}, Landroid/view/View;->setContentDescription(Ljava/lang/CharSequence;)V

    invoke-virtual {v0, p3}, Landroid/view/View;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    return-object v0
.end method

.method public onLayoutChange(Landroid/view/View;IIIIIIII)V
    .registers 10

    .line 114
    invoke-virtual {p0}, Le/e/a/PopupControls193;->layout()V

    return-void
.end method

.method public onSharedPreferenceChanged(Landroid/content/SharedPreferences;Ljava/lang/String;)V
    .registers 3

    const-string p1, "popup_info_position"
    invoke-virtual {p1, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result p1
    if-nez p1, :cond_18

    .line 111
    const-string p1, "player_button_order_v1"

    invoke-virtual {p1, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-nez p1, :cond_18

    const-string p1, "player_buttons_hidden_v1"

    invoke-virtual {p1, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-nez p1, :cond_18

    const-string p1, "player_controls_visibility_mode"

    invoke-virtual {p1, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_22

    :cond_18
    iget-object p1, p0, Le/e/a/PopupControls193;->root:Landroid/view/View;

    new-instance p2, Le/e/a/PopupControls193$$ExternalSyntheticLambda2;

    invoke-direct {p2, p0}, Le/e/a/PopupControls193$$ExternalSyntheticLambda2;-><init>(Le/e/a/PopupControls193;)V

    invoke-virtual {p1, p2}, Landroid/view/View;->post(Ljava/lang/Runnable;)Z

    :cond_22
    return-void
.end method

.method public onViewAttachedToWindow(Landroid/view/View;)V
    .registers 2

    .line 112
    invoke-virtual {p0}, Le/e/a/PopupControls193;->register()V

    invoke-virtual {p0}, Le/e/a/PopupControls193;->update()V

    return-void
.end method

.method public onViewDetachedFromWindow(Landroid/view/View;)V
    .registers 2

    .line 113
    iget-object p1, p0, Le/e/a/PopupControls193;->prefs:Landroid/content/SharedPreferences;

    invoke-interface {p1, p0}, Landroid/content/SharedPreferences;->unregisterOnSharedPreferenceChangeListener(Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;)V

    const/4 p1, 0x0

    iput-boolean p1, p0, Le/e/a/PopupControls193;->registered:Z

    return-void
.end method

.method rectangle(Landroid/view/View;III)V
    .registers 14

    .line 77
    if-nez p1, :cond_3

    return-void

    :cond_3
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    move-result-object v0

    instance-of v0, v0, Landroid/widget/RelativeLayout$LayoutParams;

    if-eqz v0, :cond_12

    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    move-result-object v0

    check-cast v0, Landroid/widget/RelativeLayout$LayoutParams;

    goto :goto_17

    :cond_12
    new-instance v0, Landroid/widget/RelativeLayout$LayoutParams;

    invoke-direct {v0, p2, p3}, Landroid/widget/RelativeLayout$LayoutParams;-><init>(II)V

    :goto_17
    iget v1, v0, Landroid/widget/RelativeLayout$LayoutParams;->width:I

    const/4 v2, 0x0

    const/4 v3, 0x1

    if-ne v1, p2, :cond_2b

    iget v1, v0, Landroid/widget/RelativeLayout$LayoutParams;->height:I

    if-ne v1, p3, :cond_2b

    iget v1, v0, Landroid/widget/RelativeLayout$LayoutParams;->topMargin:I

    if-ne v1, p4, :cond_2b

    iget v1, v0, Landroid/widget/RelativeLayout$LayoutParams;->bottomMargin:I

    if-nez v1, :cond_2b

    const/4 v1, 0x0

    goto :goto_2c

    :cond_2b
    const/4 v1, 0x1

    :goto_2c
    const/16 v4, 0xd

    const/16 v5, 0xf

    const/4 v6, 0x2

    const/4 v7, 0x3

    const/16 v8, 0xc

    filled-new-array {v6, v7, v8, v4, v5}, [I

    move-result-object v4

    const/4 v5, 0x0

    :goto_39
    const/4 v6, 0x5

    if-lt v5, v6, :cond_53

    const/16 v3, 0xa

    invoke-virtual {v0, v3}, Landroid/widget/RelativeLayout$LayoutParams;->addRule(I)V

    iput p2, v0, Landroid/widget/RelativeLayout$LayoutParams;->width:I

    iput p3, v0, Landroid/widget/RelativeLayout$LayoutParams;->height:I

    iput p4, v0, Landroid/widget/RelativeLayout$LayoutParams;->topMargin:I

    iput v2, v0, Landroid/widget/RelativeLayout$LayoutParams;->bottomMargin:I

    if-eqz v1, :cond_4e

    invoke-virtual {p1, v0}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    :cond_4e
    const/4 p2, 0x0

    invoke-virtual {p1, p2}, Landroid/view/View;->setTranslationY(F)V

    return-void

    :cond_53
    aget v6, v4, v5

    invoke-virtual {v0, v6}, Landroid/widget/RelativeLayout$LayoutParams;->getRule(I)I

    move-result v7

    if-eqz v7, :cond_5f

    invoke-virtual {v0, v6}, Landroid/widget/RelativeLayout$LayoutParams;->removeRule(I)V

    const/4 v1, 0x1

    :cond_5f
    add-int/lit8 v5, v5, 0x1

    goto :goto_39
.end method

.method register()V
    .registers 2

    .line 110
    iget-boolean v0, p0, Le/e/a/PopupControls193;->registered:Z

    if-nez v0, :cond_c

    iget-object v0, p0, Le/e/a/PopupControls193;->prefs:Landroid/content/SharedPreferences;

    invoke-interface {v0, p0}, Landroid/content/SharedPreferences;->registerOnSharedPreferenceChangeListener(Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;)V

    const/4 v0, 0x1

    iput-boolean v0, p0, Le/e/a/PopupControls193;->registered:Z

    :cond_c
    return-void
.end method

.method update()V
    .registers 7

    .line 68
    const-string v0, "info"

    iget-boolean v1, p0, Le/e/a/PopupControls193;->updating:Z

    if-eqz v1, :cond_7

    return-void

    :cond_7
    const/4 v1, 0x1

    iput-boolean v1, p0, Le/e/a/PopupControls193;->updating:Z

    .line 69
    const/4 v1, 0x0

    :try_start_b
    iget-object v2, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-virtual {v2}, Landroid/widget/LinearLayout;->removeAllViews()V

    iget-object v2, p0, Le/e/a/PopupControls193;->prefs:Landroid/content/SharedPreferences;

    invoke-static {v2}, Le/e/a/PlayerButtons193;->hidden(Landroid/content/SharedPreferences;)Ljava/util/Set;

    move-result-object v2

    .line 70
    iget-object v3, p0, Le/e/a/PopupControls193;->prefs:Landroid/content/SharedPreferences;

    invoke-static {v3}, Le/e/a/PlayerButtons193;->order(Landroid/content/SharedPreferences;)Ljava/util/List;

    move-result-object v3

    invoke-interface {v3}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_20
    :goto_20
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-nez v4, :cond_61

    .line 71
    iget-object v2, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    invoke-interface {v2, v0}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Landroid/view/View;

    if-eqz v2, :cond_38

    invoke-virtual {v2, v1}, Landroid/view/View;->setVisibility(I)V

    iget-object v3, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-static {v3, v2}, Le/e/a/Revision196;->popupInfo(Landroid/widget/LinearLayout;Landroid/view/View;)V

    .line 72
    :cond_38
    iget-object v3, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    const-string v4, "close"

    invoke-interface {v3, v4}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Landroid/view/View;

    if-eqz v3, :cond_4c

    invoke-virtual {v3, v1}, Landroid/view/View;->setVisibility(I)V

    iget-object v4, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-virtual {v4, v3}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 74
    :cond_4c
    if-eqz v2, :cond_5b

    const/4 v3, 0x0

    invoke-virtual {v2, v3}, Landroid/view/View;->setBackgroundTintList(Landroid/content/res/ColorStateList;)V

    new-instance v3, Le/e/a/PlayerIcons$Icon;

    invoke-direct {v3, v0, v1}, Le/e/a/PlayerIcons$Icon;-><init>(Ljava/lang/String;Z)V

    invoke-virtual {v2, v3}, Landroid/view/View;->setBackground(Landroid/graphics/drawable/Drawable;)V
    :try_end_5a
    .catchall {:try_start_b .. :try_end_5a} :catchall_80

    .line 75
    nop

    :cond_5b
    iput-boolean v1, p0, Le/e/a/PopupControls193;->updating:Z

    invoke-virtual {p0}, Le/e/a/PopupControls193;->layout()V

    return-void

    .line 70
    :cond_61
    :try_start_61
    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v4

    check-cast v4, Ljava/lang/String;

    iget-object v5, p0, Le/e/a/PopupControls193;->buttons:Ljava/util/Map;

    invoke-interface {v5, v4}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v5

    check-cast v5, Landroid/view/View;

    if-eqz v5, :cond_20

    invoke-interface {v2, v4}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v4

    if-nez v4, :cond_20

    invoke-virtual {v5, v1}, Landroid/view/View;->setVisibility(I)V

    iget-object v4, p0, Le/e/a/PopupControls193;->row:Landroid/widget/LinearLayout;

    invoke-virtual {v4, v5}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V
    :try_end_7f
    .catchall {:try_start_61 .. :try_end_7f} :catchall_80

    goto :goto_20

    .line 75
    :catchall_80
    move-exception v0

    iput-boolean v1, p0, Le/e/a/PopupControls193;->updating:Z

    throw v0
.end method
