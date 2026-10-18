.class public Lcom/sauzask/nicoid/NicoidVideoFragment;
.super Landroidx/fragment/app/Fragment;
.source ""

# interfaces
.implements Le/e/a/l3/g;
.implements Le/e/a/l3/l;
.implements Le/e/a/l3/j;
.implements Le/e/a/l3/m;
.implements Le/e/a/l3/c;
.implements Le/e/a/l3/f;


# static fields
.field public static V1:Le/e/a/v;


# instance fields
.field public A0:Z

.field public A1:Lcom/sauzask/nicoid/NicoidVideoActivity;

.field public B0:Z

.field public B1:Le/e/a/k3;

.field public C0:Z

.field public C1:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Le/e/a/x1;",
            ">;"
        }
    .end annotation
.end field

.field public D0:Z

.field public D1:I

.field public E0:Z

.field public E1:Lcom/sauzask/nicoid/NicoidViewPager;

.field public F0:Z

.field public F1:Lcom/astuetz/PagerSlidingTabStrip;

.field public G0:Z

.field public G1:Z

.field public H0:Z

.field public H1:Landroid/content/SharedPreferences;

.field public I0:Z

.field public I1:Landroid/content/SharedPreferences$Editor;

.field public J0:Z

.field public J1:Le/e/a/l3/a;

.field public K0:Z

.field public K1:Le/e/a/l3/e;

.field public L0:Z

.field public L1:Landroid/widget/LinearLayout;

.field public M0:Z

.field public M1:Landroid/widget/ProgressBar;

.field public N0:Z

.field public N1:Landroid/widget/TextView;

.field public O0:Z

.field public O1:I

.field public P0:Z

.field public P1:Z

.field public Q0:Z

.field public Q1:Z

.field public R0:Z

.field public R1:Z

.field public S0:Z

.field public S1:I

.field public T0:Z

.field public T1:Z

.field public U0:Z

.field public U1:I

.field public V0:Z

.field public W0:[I

.field public X0:Z

.field public Y:Landroid/os/Handler;

.field public Y0:Landroid/widget/RelativeLayout;

.field public Z:Landroid/widget/SeekBar;

.field public Z0:Landroid/widget/RelativeLayout;

.field public a0:Lcom/devbrackets/android/exomedia/ui/widget/VideoView;

.field public a1:Landroid/widget/RelativeLayout;

.field public b0:Ljava/lang/String;

.field public b1:Landroid/widget/RelativeLayout;

.field public c0:Ljava/lang/String;

.field public c1:Landroid/widget/LinearLayout;

.field public d0:I

.field public d1:Landroid/widget/LinearLayout;

.field public e0:I

.field public e1:Landroid/widget/LinearLayout;

.field public f0:I

.field public f1:Lcom/google/android/gms/ads/AdView;

.field public g0:I

.field public g1:Le/e/a/v;

.field public h0:J

.field public h1:Le/e/a/d0;

.field public i0:I

.field public i1:Le/e/a/b1;

.field public j0:I

.field public j1:Le/e/a/g0;

.field public k0:I

.field public k1:Landroid/widget/Button;

.field public l0:I

.field public l1:Landroid/widget/Button;

.field public m0:Z

.field public m1:Landroid/widget/Button;

.field public n0:I

.field public n1:Landroid/widget/Button;

.field public o0:I

.field public o1:Landroid/widget/Button;

.field public p0:Ljava/lang/String;

.field public p1:Landroid/widget/Button;

.field public q0:Lorg/apache/http/client/CookieStore;

.field public q1:Landroid/widget/Button;

.field public r0:Z

.field public r1:Landroid/widget/Button;

.field public s0:Z

.field public s1:Landroid/widget/Button;

.field public t0:Z

.field public t1:Landroid/widget/ProgressBar;

.field public u0:Z

.field public u1:Landroid/widget/TextView;

.field public v0:Z

.field public v1:Le/e/a/o2;

.field public w0:Z

.field public w1:Ljava/util/Timer;

.field public x0:Z

.field public x1:Landroid/view/View;

.field public y0:Z

.field public y1:Ljava/lang/String;

.field public z0:Z

.field public z1:Ljava/lang/Float;



.method public final f(I)V
    .registers 4
    int-to-long v0, p1
    iput-wide v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->h0:J
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->x1:Landroid/view/View;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->t1:Landroid/widget/ProgressBar;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->k1:Landroid/widget/Button;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->m1:Landroid/widget/Button;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->l1:Landroid/widget/Button;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->o1:Landroid/widget/Button;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->s1:Landroid/widget/Button;
    if-eqz v0, :metadata_detached195
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->Z:Landroid/widget/SeekBar;
    if-eqz v0, :metadata_detached195


    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->t1:Landroid/widget/ProgressBar;

    const/16 v1, 0x8

    invoke-virtual {v0, v1}, Landroid/widget/ProgressBar;->setVisibility(I)V

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->k1:Landroid/widget/Button;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setVisibility(I)V

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->C1:Ljava/util/ArrayList;

    if-eqz v0, :cond_1b

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->m1:Landroid/widget/Button;

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setVisibility(I)V

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->l1:Landroid/widget/Button;

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setVisibility(I)V

    :cond_1b
    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->k1:Landroid/widget/Button;

    new-instance v1, Lcom/sauzask/nicoid/NicoidVideoFragment$c;

    invoke-direct {v1, p0}, Lcom/sauzask/nicoid/NicoidVideoFragment$c;-><init>(Lcom/sauzask/nicoid/NicoidVideoFragment;)V

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->m1:Landroid/widget/Button;

    new-instance v1, Lcom/sauzask/nicoid/NicoidVideoFragment$d;

    invoke-direct {v1, p0}, Lcom/sauzask/nicoid/NicoidVideoFragment$d;-><init>(Lcom/sauzask/nicoid/NicoidVideoFragment;)V

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->l1:Landroid/widget/Button;

    new-instance v1, Lcom/sauzask/nicoid/NicoidVideoFragment$e;

    invoke-direct {v1, p0}, Lcom/sauzask/nicoid/NicoidVideoFragment$e;-><init>(Lcom/sauzask/nicoid/NicoidVideoFragment;)V

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->o1:Landroid/widget/Button;

    new-instance v1, Lcom/sauzask/nicoid/NicoidVideoFragment$f;

    invoke-direct {v1, p0}, Lcom/sauzask/nicoid/NicoidVideoFragment$f;-><init>(Lcom/sauzask/nicoid/NicoidVideoFragment;)V

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    int-to-long v0, p1

    iput-wide v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->h0:J

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->Z:Landroid/widget/SeekBar;

    iget-wide v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->h0:J

    long-to-int v1, v0

    invoke-virtual {p1, v1}, Landroid/widget/SeekBar;->setMax(I)V

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->x1:Landroid/view/View;

    const v0, 0x7f080085

    invoke-virtual {p1}, Landroid/view/View;->getRootView()Landroid/view/View;
    move-result-object p1
    invoke-virtual {p1, v0}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    move-result-object p1

    if-eqz p1, :duration_updated195
    check-cast p1, Landroid/widget/TextView;

    iget-wide v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->h0:J

    long-to-int v1, v0

    div-int/lit16 v1, v1, 0x3e8

    invoke-virtual {p0, v1}, Lcom/sauzask/nicoid/NicoidVideoFragment;->l(I)Ljava/lang/CharSequence;

    move-result-object v0

    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    :duration_updated195
    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->s1:Landroid/widget/Button;

    new-instance v0, Lcom/sauzask/nicoid/NicoidVideoFragment$g;

    invoke-direct {v0, p0}, Lcom/sauzask/nicoid/NicoidVideoFragment$g;-><init>(Lcom/sauzask/nicoid/NicoidVideoFragment;)V

    invoke-virtual {p1, v0}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->Z:Landroid/widget/SeekBar;

    iget-wide v0, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->h0:J

    long-to-int v1, v0

    invoke-virtual {p1, v1}, Landroid/widget/SeekBar;->setSecondaryProgress(I)V

    iget-object p1, p0, Lcom/sauzask/nicoid/NicoidVideoFragment;->Z:Landroid/widget/SeekBar;

    new-instance v0, Lcom/sauzask/nicoid/NicoidVideoFragment$h;

    invoke-direct {v0, p0}, Lcom/sauzask/nicoid/NicoidVideoFragment$h;-><init>(Lcom/sauzask/nicoid/NicoidVideoFragment;)V

    invoke-virtual {p1, v0}, Landroid/widget/SeekBar;->setOnSeekBarChangeListener(Landroid/widget/SeekBar$OnSeekBarChangeListener;)V

    :metadata_detached195
    return-void
.end method
