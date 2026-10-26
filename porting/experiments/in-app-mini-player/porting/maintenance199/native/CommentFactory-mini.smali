.class public Le/e/a/v;
.super Ljava/lang/Object;
.source ""


# instance fields
.field public a:Landroid/view/View;

.field public b:Z

.field public c:Le/e/a/l3/b;

.field public d:Ljava/lang/String;

.field public e:Le/e/a/g0;

.field public f:Ljava/lang/String;

.field public g:Ljava/lang/String;

.field public h:Ljava/lang/String;

.field public i:Ljava/lang/String;

.field public j:Ljava/lang/String;

.field public k:Z


# direct methods
.method public constructor <init>(Landroid/content/Context;Le/e/a/g0;)V
    .registers 5

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    invoke-static {}, Le/e/a/v0;->f()I

    move-result v0

    const/16 v1, 0x12

    if-eq v0, v1, :cond_17

    new-instance v0, Le/e/a/t;

    invoke-direct {v0, p1, p0}, Le/e/a/t;-><init>(Landroid/content/Context;Le/e/a/v;)V

    iput-object v0, p0, Le/e/a/v;->a:Landroid/view/View;

    iget-object p1, p0, Le/e/a/v;->a:Landroid/view/View;

    check-cast p1, Le/e/a/t;

    goto :goto_22

    :cond_17
    new-instance v0, Le/e/a/u;

    invoke-direct {v0, p1, p0}, Le/e/a/u;-><init>(Landroid/content/Context;Le/e/a/v;)V

    iput-object v0, p0, Le/e/a/v;->a:Landroid/view/View;

    iget-object p1, p0, Le/e/a/v;->a:Landroid/view/View;

    check-cast p1, Le/e/a/u;

    :goto_22
    iput-object p1, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    iput-object p2, p0, Le/e/a/v;->e:Le/e/a/g0;

    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Le/e/a/g0;Ljava/lang/Float;Landroid/widget/LinearLayout;)V
    .registers 7

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/16 v1, 0x12

    instance-of v0, p1, Lcom/sauzask/nicoid/NicoidPopupViewService;

    if-nez v0, :cond_1b

    invoke-static {}, Le/e/a/v0;->f()I

    move-result v0

    if-eq v0, v1, :cond_1b

    new-instance v0, Le/e/a/t;

    invoke-direct {v0, p1, p3, p0}, Le/e/a/t;-><init>(Landroid/content/Context;Ljava/lang/Float;Le/e/a/v;)V

    iput-object v0, p0, Le/e/a/v;->a:Landroid/view/View;

    iget-object p1, p0, Le/e/a/v;->a:Landroid/view/View;

    check-cast p1, Le/e/a/t;

    goto :goto_26

    :cond_1b
    new-instance v0, Le/e/a/u;

    invoke-direct {v0, p1, p3, p0}, Le/e/a/u;-><init>(Landroid/content/Context;Ljava/lang/Float;Le/e/a/v;)V

    iput-object v0, p0, Le/e/a/v;->a:Landroid/view/View;

    iget-object p1, p0, Le/e/a/v;->a:Landroid/view/View;

    check-cast p1, Le/e/a/u;

    :goto_26
    iput-object p1, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    iget-object p1, p0, Le/e/a/v;->a:Landroid/view/View;

    invoke-virtual {p4, p1}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    iput-object p2, p0, Le/e/a/v;->e:Le/e/a/g0;

    return-void
.end method


# virtual methods
.method public a(Z)Ljava/lang/Boolean;
    .registers 3

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0, p1}, Le/e/a/l3/b;->b(Z)Ljava/lang/Boolean;

    move-result-object p1

    return-object p1
.end method

.method public a()V
    .registers 2

    const/4 v0, 0x0

    iput-object v0, p0, Le/e/a/v;->d:Ljava/lang/String;

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0}, Le/e/a/l3/b;->d()V

    return-void
.end method

.method public a(Le/e/a/v;)V
    .registers 4

    iget-object v0, p1, Le/e/a/v;->d:Ljava/lang/String;

    iput-object v0, p0, Le/e/a/v;->d:Ljava/lang/String;

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    iget-object v1, p1, Le/e/a/v;->a:Landroid/view/View;

    invoke-interface {v0, v1}, Le/e/a/l3/b;->setRyuuyou(Landroid/view/View;)V

    iget-object v0, p1, Le/e/a/v;->g:Ljava/lang/String;

    iput-object v0, p0, Le/e/a/v;->g:Ljava/lang/String;

    iget-object v0, p1, Le/e/a/v;->h:Ljava/lang/String;

    iput-object v0, p0, Le/e/a/v;->h:Ljava/lang/String;

    iget-object v0, p1, Le/e/a/v;->i:Ljava/lang/String;

    iput-object v0, p0, Le/e/a/v;->i:Ljava/lang/String;

    iget-object v0, p1, Le/e/a/v;->f:Ljava/lang/String;

    iput-object v0, p0, Le/e/a/v;->f:Ljava/lang/String;

    iget-object p1, p1, Le/e/a/v;->j:Ljava/lang/String;

    iput-object p1, p0, Le/e/a/v;->j:Ljava/lang/String;

    return-void
.end method

.method public a(Ljava/util/ArrayList;)V
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Le/e/a/q;",
            ">;)V"
        }
    .end annotation

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0, p1}, Le/e/a/l3/b;->setcomm(Ljava/util/ArrayList;)V

    return-void
.end method

.method public a(Le/e/a/n0;)Z
    .registers 4

    .line 1
    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    const/4 v1, 0x1

    invoke-interface {v0, p1, v1}, Le/e/a/l3/b;->a(Le/e/a/n0;Z)Z

    move-result p1

    return p1
.end method

.method public b()Ljava/util/ArrayList;
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList<",
            "Le/e/a/q;",
            ">;"
        }
    .end annotation

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0}, Le/e/a/l3/b;->getCommentList()Ljava/util/ArrayList;

    move-result-object v0

    return-object v0
.end method

.method public b(Ljava/util/ArrayList;)V
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Le/e/a/q;",
            ">;)V"
        }
    .end annotation

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0, p1}, Le/e/a/l3/b;->setfork(Ljava/util/ArrayList;)V

    return-void
.end method

.method public c(Ljava/util/ArrayList;)V
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList<",
            "Le/e/a/q;",
            ">;)V"
        }
    .end annotation

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0, p1}, Le/e/a/l3/b;->settoucomm(Ljava/util/ArrayList;)V

    return-void
.end method

.method public c()Z
    .registers 2

    iget-object v0, p0, Le/e/a/v;->c:Le/e/a/l3/b;

    invoke-interface {v0}, Le/e/a/l3/b;->i()Z

    move-result v0

    return v0
.end method
