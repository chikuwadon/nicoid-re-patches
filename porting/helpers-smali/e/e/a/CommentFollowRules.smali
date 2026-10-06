.class public final Le/e/a/CommentFollowRules;
.super Ljava/lang/Object;
.source "CommentFollowRules.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .line 2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static nearest([JJ)I
    .registers 14

    .line 3
    const/4 v0, -0x1

    const-wide v1, 0x7fffffffffffffffL

    const/4 v3, 0x0

    move-wide v4, v1

    :goto_8
    array-length v6, p0

    if-ge v3, v6, :cond_2a

    aget-wide v6, p0, v3

    const-wide/16 v8, 0x0

    cmp-long v10, v6, v8

    if-ltz v10, :cond_27

    cmp-long v10, v6, v1

    if-nez v10, :cond_18

    goto :goto_27

    :cond_18
    invoke-static {v8, v9, p1, p2}, Ljava/lang/Math;->max(JJ)J

    move-result-wide v8

    sub-long/2addr v6, v8

    invoke-static {v6, v7}, Ljava/lang/Math;->abs(J)J

    move-result-wide v6

    cmp-long v8, v6, v4

    if-gez v8, :cond_27

    move v0, v3

    move-wide v4, v6

    :cond_27
    :goto_27
    add-int/lit8 v3, v3, 0x1

    goto :goto_8

    :cond_2a
    return v0
.end method
