.class public Lcom/sauzask/nicoid/NicoidVideoListActivity;
.super Lcom/sauzask/nicoid/NicoidActivity;
.source ""


# static fields
.field public static I:Z

.field public static J:I


# instance fields
.field public A:Ljava/lang/String;

.field public B:Ljava/lang/String;

.field public C:Ljava/lang/String;

.field public D:I

.field public E:I

.field public F:Lcom/sauzask/nicoid/NicoidVideoListFragment;

.field public G:Landroid/content/SharedPreferences;

.field public H:Lcom/google/android/gms/ads/AdView;

.field public y:Landroid/widget/LinearLayout;

.field public z:Landroid/widget/RelativeLayout;



.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .locals 5

    iget-object v0, p0, Lcom/sauzask/nicoid/NicoidVideoListActivity;->A:Ljava/lang/String;

    const/4 v1, 0x1

    const/4 v2, 0x0

    if-eqz v0, :cond_0

    const-string v3, "https://nvapi.nicovideo.jp/v1/search/video\\?(tag|keyword)=([^&]+)&([^$]+)"

    invoke-virtual {v0, v3}, Ljava/lang/String;->matches(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    const v0, 0x7f0f0118

    invoke-virtual {p0, v0}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v3

    invoke-static/range {v3 .. v3}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-interface {p1, v2, v0, v2, v3}, Landroid/view/Menu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    move-result-object v0

    .line 1
    invoke-interface {v0, v1}, Landroid/view/MenuItem;->setShowAsAction(I)V

    const v3, 0x7f070020

    .line 2
    invoke-interface {v0, v3}, Landroid/view/MenuItem;->setIcon(I)Landroid/view/MenuItem;

    :cond_0
    iget v0, p0, Lcom/sauzask/nicoid/NicoidVideoListActivity;->E:I

    const/4 v3, 0x4

    const v4, 0x7f0f020a

    if-ne v0, v3, :cond_1

    invoke-virtual {p0, v4}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-static/range {v0 .. v0}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-interface {p1, v2, v4, v2, v0}, Landroid/view/Menu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    goto :goto_2

    :cond_1
    const/16 v3, 0x8

    if-ne v0, v3, :cond_2

    const v0, 0x7f0f020b

    invoke-virtual {p0, v4}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v3

    invoke-static/range {v3 .. v3}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-interface {p1, v2, v0, v2, v3}, Landroid/view/Menu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    goto :goto_2

    :cond_2
    const/4 v3, 0x5

    if-ne v0, v3, :cond_3

    const/4 v0, 0x1

    goto :goto_0

    :cond_3
    const/4 v0, 0x0

    :goto_0
    iget v3, p0, Lcom/sauzask/nicoid/NicoidVideoListActivity;->E:I

    const/16 v4, 0x9

    if-ne v3, v4, :cond_4

    const/4 v3, 0x1

    goto :goto_1

    :cond_4
    const/4 v3, 0x0

    :goto_1
    or-int/2addr v0, v3

    if-eqz v0, :cond_5

    const v0, 0x7f0f0224

    invoke-virtual {p0, v0}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-static/range {v0 .. v0}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    invoke-interface {p1, v2, v1, v2, v0}, Landroid/view/Menu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    :cond_5
    :goto_2
    const v0, 0x7f0f0201

    invoke-virtual {p0, v0}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v3

    invoke-static/range {v3 .. v3}, Le/e/a/UiStrings;->translate(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-interface {p1, v2, v0, v2, v3}, Landroid/view/Menu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    move-result-object v0

    .line 3
    invoke-interface {v0, v2}, Landroid/view/MenuItem;->setShowAsAction(I)V

    .line 4
    invoke-interface {v0, v1}, Landroid/view/MenuItem;->setCheckable(Z)Landroid/view/MenuItem;

    sget-boolean v1, Lcom/sauzask/nicoid/NicoidVideoListActivity;->I:Z

    invoke-interface {v0, v1}, Landroid/view/MenuItem;->setChecked(Z)Landroid/view/MenuItem;

    invoke-super {p0, p1}, Lcom/sauzask/nicoid/NicoidActivity;->onCreateOptionsMenu(Landroid/view/Menu;)Z

    move-result v0

    invoke-static {p0, p1}, Le/e/a/ListActions;->adjustHistoryMenu(Ljava/lang/Object;Landroid/view/Menu;)V

    return v0
.end method
