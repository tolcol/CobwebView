## CobwebView

[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v/https/dl.bintray.com/tolcol/maven/com/tolcol/lib/cobwebview/maven-metadata.xml.svg?color=blue&label=jcenter)](https://dl.bintray.com/tolcol/maven/com/tolcol/lib/cobwebview/)

CobwebView is a custom view that presents data from multiple angles, also called as Radar chart.

## How to use
in build.gradle
```groovy
implementation 'com.tolcol.lib:Cobwebview:0.0.2'
```
in xml
```xml
<com.tolcol.lib.cobweb.CobwebView
    android:id="@+id/cobweb"
    android:layout_width="match_parent"
    android:layout_height="300dp"
    app:cbvColor="@color/black"
    app:cbvScale="0.7"
    app:cbvScoreFill="false"
    app:cbvScoreMax="10"
    app:cbvScorePoint="false"
    app:cbvScoreStep="2"
    app:cbvScoreStrokeWidth="1dp"
    app:cbvStrokeWidth="1dp"
    app:cbvTitleColor="@color/colorPrimary"
    app:cbvTitleSize="14sp"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    />
```

Attributes
```Attributes
    <declare-styleable name="CobwebView">
        <!--cbvColor 背景线条颜色 -->
        <!--cbvStrokeWidth 背景线条宽度-->
        <!--cbvScale 蛛网半径占宽度的比-->
        <attr name="cbvScale" format="float"/>
        <!--cbvScoreMax 最大分数-->
        <!--cbvScoreStep 分数分割-->
        <!--cbvScorePoint 是否绘制分数点-->
        <!--cbvScoreStrokeWidth 分数线条宽度-->
        <!--cbvScoreFill 是否填充-->
        <!--cbvTitleSize 文字大小-->
        <!--cbvTitleColor 标题颜色-->
    </declare-styleable>
```
## Feature

The current version is 0.0.2, which is an experimental version. The function has not been fully 
developed yet, so stay tuned.