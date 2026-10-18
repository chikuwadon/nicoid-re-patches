.class public Lcom/sauzask/nicoid/NicoidChormecastSenderService;
.super Landroid/app/Service;
.source ""

# interfaces
.implements Le/e/a/l3/m;


# static fields
.field public static D:Z = false

.field public static E:Le/e/a/v; = null

.field public static F:Ljava/util/ArrayList; = null
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList<",
            "Le/e/a/x1;",
            ">;"
        }
    .end annotation
.end field

.field public static G:Z = false

.field public static H:I = 0x0

.field public static I:Ljava/lang/String; = ""

.field public static J:Ljava/lang/String; = ""

.field public static K:Le/e/a/l3/a;

.field public static L:Landroid/os/Bundle;

.field public static M:Ljava/lang/String;

.field public static N:I

.field public static O:I

.field public static P:Z

.field public static Q:Z


# instance fields
.field public A:Le/d/b/b/d/a$d;

.field public B:Le/d/b/b/e/k/e$b;

.field public C:Le/d/b/b/e/k/e$c;

.field public a:I

.field public b:Ljava/lang/String;

.field public c:Le/d/b/b/e/k/e;

.field public d:Lcom/google/android/gms/cast/CastDevice;

.field public e:Ljava/lang/String;

.field public f:Ljava/lang/String;

.field public g:D

.field public i:Ljava/lang/String;

.field public j:Ljava/lang/String;

.field public k:Ljava/lang/String;

.field public l:Ljava/lang/String;

.field public m:Landroid/content/Context;

.field public n:Le/e/a/o2;

.field public o:Landroid/content/SharedPreferences;

.field public p:Z

.field public q:Z

.field public r:Z

.field public s:Z

.field public t:Ljava/lang/String;

.field public u:Le/e/a/d0;

.field public v:Ljava/lang/String;

.field public w:Le/e/a/g0;

.field public x:Landroid/os/Handler;

.field public y:Le/e/a/p2;

.field public final z:Le/d/b/b/d/a$e;



.method public e()V
    .registers 8

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v1, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->i:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "comment.json"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v2, " / "

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    iget-object v2, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->j:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    new-instance v0, Lorg/json/JSONObject;

    invoke-direct {v0}, Lorg/json/JSONObject;-><init>()V

    const/4 v2, 0x1

    const/4 v3, 0x0

    :try_start_23
    const-string v4, "type"

    invoke-virtual {v0, v4, v3}, Lorg/json/JSONObject;->put(Ljava/lang/String;I)Lorg/json/JSONObject;

    const-string v4, "loadurl"

    iget-object v5, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->j:Ljava/lang/String;

    invoke-virtual {v0, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v4, "videoid"

    sget-object v5, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->I:Ljava/lang/String;

    invoke-virtual {v0, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v4, "title"

    iget-object v5, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->k:Ljava/lang/String;

    invoke-virtual {v0, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;

    const-string v4, "comhide"

    iget-object v5, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->o:Landroid/content/SharedPreferences;

    const-string v6, "comment_hide"

    invoke-interface {v5, v6, v3}, Landroid/content/SharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v5

    invoke-virtual {v0, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Z)Lorg/json/JSONObject;

    const-string v4, "commin"

    iget-object v5, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->o:Landroid/content/SharedPreferences;

    const-string v6, "comment_cast_min"

    invoke-interface {v5, v6, v2}, Landroid/content/SharedPreferences;->getBoolean(Ljava/lang/String;Z)Z

    move-result v5

    invoke-virtual {v0, v4, v5}, Lorg/json/JSONObject;->put(Ljava/lang/String;Z)Lorg/json/JSONObject;

    const-string v4, "commenturl"

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v6, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->i:Ljava/lang/String;

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v4, v1}, Lorg/json/JSONObject;->put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    :try_end_6d
    .catch Ljava/lang/Exception; {:try_start_23 .. :try_end_6d} :catch_6e

    goto :goto_6f

    :catch_6e
    nop

    :goto_6f
    sget-object v1, Le/d/b/b/d/a;->c:Le/d/b/b/d/a$b;

    iget-object v4, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->c:Le/d/b/b/e/k/e;

    iget-object v5, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->f:Ljava/lang/String;

    invoke-virtual {v0}, Lorg/json/JSONObject;->toString()Ljava/lang/String;

    move-result-object v0

    check-cast v1, Le/d/b/b/d/a$b$a;

    invoke-virtual {v1, v4, v5, v0}, Le/d/b/b/d/a$b$a;->a(Le/d/b/b/e/k/e;Ljava/lang/String;Ljava/lang/String;)Le/d/b/b/e/k/g;
    move-result-object v0
    invoke-static {v0}, Le/e/a/CastDiagnostics;->sent(Ljava/lang/Object;)V

    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "http://www.nicovideo.jp/watch/"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sget-object v1, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->I:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Le/e/a/CachePlayback;->parse(Ljava/lang/String;)Landroid/net/Uri;

    move-result-object v0

    const-string v1, "android.intent.action.VIEW"

    const-string v4, "com.sauzask.nicoid.hls"

    const-string v5, "com.sauzask.nicoid.NicoidVideoActivity"

    invoke-static {v1, v0, v4, v5}, Le/b/a/a/a;->a(Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    move-result-object v0

    const-string v1, "intentselect"

    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    sget v1, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->H:I

    const-string v2, "playlistposition"

    invoke-virtual {v0, v2, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    const/high16 v1, 0x10000000

    invoke-virtual {v0, v1}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    iget-object v1, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->m:Landroid/content/Context;

    invoke-static {v1, v3, v0, v3}, Landroid/app/PendingIntent;->getActivity(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    move-result-object v0

    iget-object v1, p0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->k:Ljava/lang/String;

    const/4 v2, 0x0

    const-string v3, "\u518d\u751f\u4e2d"

    invoke-virtual {p0, v1, v3, v2, v0}, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/app/PendingIntent;)V

    sget-object v0, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->K:Le/e/a/l3/a;

    if-eqz v0, :cond_c5

    invoke-interface {v0}, Le/e/a/l3/a;->l()V

    :cond_c5
    const-string v0, "chormecast : \u518d\u751f\u6e96\u5099\u5b8c\u4e86"

    invoke-static {v0}, Le/b/a/a/a;->a(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    sget-object v1, Lcom/sauzask/nicoid/NicoidChormecastSenderService;->L:Landroid/os/Bundle;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    return-void
.end method
