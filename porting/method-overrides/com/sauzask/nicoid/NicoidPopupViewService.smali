.class public Lcom/sauzask/nicoid/NicoidPopupViewService;
.super Landroid/app/Service;
.source ""

# interfaces
.implements Landroid/view/View$OnTouchListener;
.implements Le/e/a/l3/h;


# static fields
.field public static h0:Le/e/a/n0; = null

.field public static i0:Z = false

.field public static j0:Z = false

.field public static k0:Le/e/a/v; = null

.field public static l0:Le/e/a/l3/i; = null

.field public static m0:Ljava/lang/Float; = null

.field public static n0:Z = false

.field public static o0:Z = false

.field public static p0:Z = false

.field public static q0:Ljava/lang/String; = ""

.field public static r0:Ljava/lang/String; = null

.field public static s0:Landroid/os/Bundle; = null

.field public static t0:I = 0x0

.field public static u0:I = 0x0

.field public static v0:I = 0x0

.field public static w0:F = 350.0f


# instance fields
.field public A:Le/e/a/g0;

.field public B:Landroid/widget/Button;

.field public C:Landroid/widget/Button;

.field public D:Landroid/widget/Button;

.field public E:Landroid/widget/Button;

.field public F:Landroid/widget/Button;

.field public G:Landroid/widget/Button;

.field public H:Landroid/widget/ProgressBar;

.field public I:Landroid/widget/TextView;

.field public J:Le/e/a/o2;

.field public K:Ljava/util/Timer;

.field public L:Ljava/lang/String;

.field public M:Ljava/lang/String;

.field public N:Ljava/lang/String;

.field public O:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Le/e/a/x1;",
            ">;"
        }
    .end annotation
.end field

.field public P:I

.field public Q:Landroid/content/SharedPreferences;

.field public R:Landroid/content/SharedPreferences$Editor;

.field public S:F

.field public T:F

.field public U:Landroid/view/WindowManager$LayoutParams;

.field public V:Landroid/media/AudioManager;

.field public W:Landroid/media/RemoteControlClient;

.field public X:Z

.field public Y:Landroid/view/GestureDetector;

.field public Z:Ld/h/d/e;

.field public a:Landroid/view/View;

.field public a0:Landroid/app/PendingIntent;

.field public b:Landroid/view/WindowManager;

.field public b0:Landroid/content/Context;

.field public c:Landroid/os/Handler;

.field public c0:I

.field public d:Landroid/widget/SeekBar;

.field public d0:I

.field public directPopup:Z

.field public directPopupStarted:Z

.field public e:Lcom/devbrackets/android/exomedia/ui/widget/VideoView;

.field public e0:Le/e/a/p2;

.field public f:Ljava/lang/String;

.field public f0:F

.field public g:I

.field public g0:[I

.field public i:I

.field public j:I

.field public k:I

.field public l:I

.field public m:F

.field public n:F

.field public o:I

.field public p:I

.field public q:Z

.field public r:Z

.field public s:Z

.field public t:Z

.field public u:Z

.field public v:Z

.field public w:Landroid/widget/RelativeLayout;

.field public x:Landroid/widget/RelativeLayout;

.field public y:Landroid/widget/LinearLayout;

.field public z:Le/e/a/d0;



.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .registers 16

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->Y:Landroid/view/GestureDetector;

    invoke-virtual {v0, p2}, Landroid/view/GestureDetector;->onTouchEvent(Landroid/view/MotionEvent;)Z

    move-result v0

    const/4 v1, 0x1

    if-eqz v0, :cond_a

    return v1

    :cond_a
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getAction()I

    move-result v0

    const/4 v2, 0x2

    const/4 v3, 0x3

    const/4 v4, 0x0

    if-eqz v0, :cond_1ea

    const/4 v5, 0x0

    if-eq v0, v1, :cond_1e0

    if-eq v0, v2, :cond_1a

    goto/16 :goto_284

    :cond_1a
    :try_start_1a
    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result v0

    float-to-int v0, v0

    iget v6, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->l:I

    sub-int/2addr v0, v6

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawX()F

    move-result p2

    float-to-int p2, p2

    iget-object v6, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget v6, v6, Landroid/view/WindowManager$LayoutParams;->y:I

    sub-int v6, v0, v6

    iget-object v7, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget v7, v7, Landroid/view/WindowManager$LayoutParams;->x:I

    sub-int v7, p2, v7

    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    move-result v8

    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    move-result p1

    int-to-float p2, p2

    iget v9, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->n:F

    sub-float/2addr p2, v9

    float-to-int p2, p2

    int-to-float v9, v0

    iget v10, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->m:F

    sub-float/2addr v9, v10

    float-to-int v9, v9

    iget v10, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F
    :try_end_47
    .catch Ljava/lang/Exception; {:try_start_1a .. :try_end_47} :catch_1da

    const-string v11, "pop_poy"

    const-string v12, "pop_pox"

    cmpl-float v5, v10, v5

    if-eqz v5, :cond_15d

    :try_start_4f
    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget p1, p1, v2

    if-eqz p1, :cond_84

    if-eq p1, v1, :cond_77

    if-eq p1, v2, :cond_6b

    if-eq p1, v3, :cond_5f

    const/4 p1, 0x0

    :goto_5c
    const/4 p2, 0x0

    :goto_5d
    const/4 v0, 0x0

    goto :goto_8f

    :cond_5f
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget p2, p2, v3

    sub-int/2addr v0, p2

    int-to-float p2, v0

    add-float/2addr p1, p2

    float-to-int p1, p1

    const/4 p2, 0x1

    goto :goto_5d

    :cond_6b
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget p2, p2, v3

    sub-int/2addr v0, p2

    int-to-float p2, v0

    sub-float/2addr p1, p2

    float-to-int p1, p1

    const/4 p2, 0x0

    goto :goto_82

    :cond_77
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget p2, p2, v3

    sub-int/2addr v0, p2

    int-to-float p2, v0

    sub-float/2addr p1, p2

    float-to-int p1, p1

    const/4 p2, 0x1

    :goto_82
    const/4 v0, 0x1

    goto :goto_8f

    :cond_84
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget p2, p2, v3

    sub-int/2addr v0, p2

    int-to-float p2, v0

    add-float/2addr p1, p2

    float-to-int p1, p1

    goto :goto_5c

    :goto_8f
    int-to-float p1, p1

    iget v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->S:F

    cmpl-float v2, p1, v2

    if-lez v2, :cond_9b

    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->S:F

    :cond_98
    :goto_98
    sput p1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    goto :goto_a4

    :cond_9b
    iget v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->T:F

    cmpg-float v2, p1, v2

    if-gez v2, :cond_98

    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->T:F

    goto :goto_98

    :goto_a4
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->p:I

    iget v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->l:I

    sub-int/2addr p1, v2

    sget v2, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    int-to-float p1, p1

    cmpl-float v2, v2, p1

    if-ltz v2, :cond_b2

    sput p1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    :cond_b2
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->i:I

    if-eqz p1, :cond_c1

    sget p1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    iget v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->i:I

    int-to-float v2, v2

    mul-float p1, p1, v2

    iget v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g:I

    int-to-float v2, v2

    goto :goto_c9

    :cond_c1
    sget p1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    const/high16 v2, 0x41800000    # 16.0f

    mul-float p1, p1, v2

    const/high16 v2, 0x41100000    # 9.0f

    :goto_c9
    div-float/2addr p1, v2

    if-eqz p2, :cond_d8

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget-object v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget v2, v2, v4

    int-to-float v2, v2

    sub-float/2addr v2, p1

    float-to-int v2, v2

    iput v2, p2, Landroid/view/WindowManager$LayoutParams;->x:I

    goto :goto_e0

    :cond_d8
    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget-object v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget v2, v2, v4

    iput v2, p2, Landroid/view/WindowManager$LayoutParams;->x:I

    :goto_e0
    if-eqz v0, :cond_f0

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget v0, v0, v1

    int-to-float v0, v0

    sget v1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    sub-float/2addr v0, v1

    float-to-int v0, v0

    iput v0, p2, Landroid/view/WindowManager$LayoutParams;->y:I

    goto :goto_f8

    :cond_f0
    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aget v0, v0, v1

    iput v0, p2, Landroid/view/WindowManager$LayoutParams;->y:I

    :goto_f8
    sget-object p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->k0:Le/e/a/v;

    new-instance v0, Landroid/widget/LinearLayout$LayoutParams;

    float-to-int p1, p1

    sget v1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    float-to-int v1, v1

    invoke-direct {v0, p1, v1}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    .line 1
    iget-object p2, p2, Le/e/a/v;->a:Landroid/view/View;

    invoke-virtual {p2, v0}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 2
    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->e:Lcom/devbrackets/android/exomedia/ui/widget/VideoView;

    new-instance v0, Landroid/widget/LinearLayout$LayoutParams;

    sget v1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    float-to-int v1, v1

    invoke-direct {v0, p1, v1}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    invoke-virtual {p2, v0}, Landroid/widget/RelativeLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->w:Landroid/widget/RelativeLayout;

    new-instance v0, Landroid/widget/LinearLayout$LayoutParams;

    sget v1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    float-to-int v1, v1

    invoke-direct {v0, p1, v1}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    invoke-virtual {p2, v0}, Landroid/widget/RelativeLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget p2, p2, Landroid/view/WindowManager$LayoutParams;->x:I

    sput p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->u0:I

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget p2, p2, Landroid/view/WindowManager$LayoutParams;->y:I

    sput p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->v0:I

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    const-string v0, "pop_ivw"

    invoke-interface {p2, v0, p1}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    const-string p2, "pop_poh"

    sget v0, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    float-to-int v0, v0

    invoke-interface {p1, p2, v0}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    sget p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->u0:I

    invoke-interface {p1, v12, p2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    sget p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->v0:I

    invoke-interface {p1, v11, p2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    invoke-interface {p1}, Landroid/content/SharedPreferences$Editor;->commit()Z

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->b:Landroid/view/WindowManager;

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->a:Landroid/view/View;

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    :goto_158
    invoke-interface {p1, p2, v0}, Landroid/view/WindowManager;->updateViewLayout(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    goto/16 :goto_284

    :cond_15d
    iget v1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->o:I
    invoke-static {p2, v1, v8}, Le/e/a/PopupDrag195;->clamp(III)I
    move-result v5
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;
    iput v5, v0, Landroid/view/WindowManager$LayoutParams;->x:I
    sub-int/2addr p2, v5
    int-to-float p2, p2
    iget v1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->n:F
    add-float/2addr p2, v1
    iput p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->n:F
    iget v1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->p:I
    iget v2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->l:I
    sub-int/2addr v1, v2
    invoke-static {v9, v1, p1}, Le/e/a/PopupDrag195;->clamp(III)I
    move-result v5
    iput v5, v0, Landroid/view/WindowManager$LayoutParams;->y:I
    sub-int/2addr v9, v5
    int-to-float v9, v9
    iget v1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->m:F
    add-float/2addr v9, v1
    iput v9, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->m:F
    iget p2, v0, Landroid/view/WindowManager$LayoutParams;->x:I
    sput p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->u0:I
    sput v5, Lcom/sauzask/nicoid/NicoidPopupViewService;->v0:I

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    sget p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->u0:I

    invoke-interface {p1, v12, p2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    sget p2, Lcom/sauzask/nicoid/NicoidPopupViewService;->v0:I

    invoke-interface {p1, v11, p2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->R:Landroid/content/SharedPreferences$Editor;

    invoke-interface {p1}, Landroid/content/SharedPreferences$Editor;->commit()Z

    sget-boolean p1, Lcom/sauzask/nicoid/NicoidPopupViewService;->o0:Z

    if-eqz p1, :cond_284

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->b:Landroid/view/WindowManager;

    iget-object p2, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->a:Landroid/view/View;

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;
    :try_end_1d8
    .catch Ljava/lang/Exception; {:try_start_4f .. :try_end_1d8} :catch_1da

    goto/16 :goto_158

    :catch_1da
    move-exception p1

    invoke-virtual {p1}, Ljava/lang/Exception;->printStackTrace()V

    goto/16 :goto_284

    :cond_1e0
    iget p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    cmpl-float p1, p1, v5

    if-eqz p1, :cond_284

    iput v5, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    goto/16 :goto_284

    :cond_1ea
    new-instance v0, Landroid/graphics/Rect;

    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    iget-object v5, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->a:Landroid/view/View;

    invoke-virtual {v5, v0}, Landroid/view/View;->getWindowVisibleDisplayFrame(Landroid/graphics/Rect;)V

    iget v0, v0, Landroid/graphics/Rect;->top:I

    iput v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->l:I

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->U:Landroid/view/WindowManager$LayoutParams;

    iget v5, v0, Landroid/view/WindowManager$LayoutParams;->x:I

    iget v0, v0, Landroid/view/WindowManager$LayoutParams;->y:I

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result v6

    float-to-int v6, v6

    iget v7, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->l:I

    sub-int/2addr v6, v7

    sub-int/2addr v6, v0

    int-to-float v6, v6

    iput v6, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->m:F

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawX()F

    move-result v6

    float-to-int v6, v6

    sub-int/2addr v6, v5

    int-to-float v6, v6

    iput v6, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->n:F

    const/high16 v6, 0x41f00000    # 30.0f

    sget-object v7, Lcom/sauzask/nicoid/NicoidPopupViewService;->m0:Ljava/lang/Float;

    invoke-virtual {v7}, Ljava/lang/Float;->floatValue()F

    move-result v7

    mul-float v7, v7, v6

    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    move-result v6

    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    move-result p1

    iget v8, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->m:F

    const/4 v9, -0x1

    cmpg-float v10, v8, v7

    if-gtz v10, :cond_24a

    iget v8, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->n:F

    cmpg-float v10, v8, v7

    if-gtz v10, :cond_23b

    iget-object v7, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    add-int/2addr v5, p1

    aput v5, v7, v4

    add-int/2addr v0, v6

    aput v0, v7, v1

    goto :goto_26e

    :cond_23b
    int-to-float p1, p1

    sub-float/2addr p1, v8

    cmpg-float p1, p1, v7

    if-gtz p1, :cond_26d

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aput v5, p1, v4

    add-int/2addr v0, v6

    aput v0, p1, v1

    const/4 v1, 0x2

    goto :goto_26e

    :cond_24a
    int-to-float v6, v6

    sub-float/2addr v6, v8

    cmpg-float v6, v6, v7

    if-gtz v6, :cond_26d

    iget v6, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->n:F

    cmpg-float v8, v6, v7

    if-gtz v8, :cond_25f

    iget-object v6, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    add-int/2addr v5, p1

    aput v5, v6, v4

    aput v0, v6, v1

    const/4 v1, 0x3

    goto :goto_26e

    :cond_25f
    int-to-float p1, p1

    sub-float/2addr p1, v6

    cmpg-float p1, p1, v7

    if-gtz p1, :cond_26d

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aput v5, p1, v4

    aput v0, p1, v1

    const/4 v1, 0x0

    goto :goto_26e

    :cond_26d
    const/4 v1, -0x1

    :goto_26e
    if-eq v1, v9, :cond_284

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    invoke-virtual {p2}, Landroid/view/MotionEvent;->getRawY()F

    move-result p2

    float-to-int p2, p2

    iget v0, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->l:I

    sub-int/2addr p2, v0

    aput p2, p1, v3

    sget p1, Lcom/sauzask/nicoid/NicoidPopupViewService;->w0:F

    iput p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->f0:F

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidPopupViewService;->g0:[I

    aput v1, p1, v2

    :cond_284
    :goto_284
    return v4
.end method
