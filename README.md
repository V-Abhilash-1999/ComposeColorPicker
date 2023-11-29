# ComposeColorPicker
## Color Picker Written in Compose

**_Contains Hue Panel_**
```Kotlin
val initialHSV = argbToHsv(Color.Cyan.toArgb())
val hsv = rememberSaveable { 
    mutableStateOf(
        Triple(initialHSV[0], initialHSV[1], initialHSV[2])
    )
}

HueBar(
    modifier = Modifier.width(300.dp),
    height = 50.dp,
    shape = RoundedCornerShape(50),
    initColor = initialHSV[0],
    selectorRadius = 25.dp,
    selectorStroke = 3.dp,
    setColor = { hue ->
        hsv.value = Triple(hue, hsv.value.second, hsv.value.third)
    }
)
```

<img src="img/Hue.png?raw=true" width="500" height="100"/>

**_Contains Saturation Value Panel_**
```Kotlin
val initialHSV = argbToHsv(Color.Cyan.toArgb())
val hsv = rememberSaveable { 
    mutableStateOf(
        Triple(initialHSV[0], initialHSV[1], initialHSV[2])
    )
}

SaturationValuePanel(
    hue = hsv.value.first,
    modifier = Modifier.width(300.dp),
    cornerRadius = 12.dp,
    aspectRatio = 1f,
    setSatVal = { sat, value ->
        hsv.value = Triple(hsv.value.first, sat, value)
    }
)
```

<img height="300" src="img/SatVal.png?raw=true" width="300"/></img>

#
###### **_Final Output_**
```Kotlin
MaterialTheme {
    val initialHSV = argbToHsv(Color.Cyan.toArgb())
    val hsv = rememberSaveable {
        mutableStateOf(
            Triple(initialHSV[0], initialHSV[1], initialHSV[2])
        )
    }
    
    ColorPicker(initialHSV = initialHSV, hsv = hsv)
}
```

<img src="img/ColorPicker(Android).gif?raw=true"></img>
<img height="400" src="img/ColorPicker(iOS).png?raw=true"/></img>
<img height="400" src="img/ColorPicker(Mac).png?raw=true"/></img>
<img height="400" src="img/ColorPicker(Windows).png?raw=true"/></img>
