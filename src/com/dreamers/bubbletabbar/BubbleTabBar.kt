package com.dreamers.bubbletabbar

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import com.google.appinventor.components.annotations.DesignerProperty
import com.google.appinventor.components.annotations.SimpleEvent
import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.annotations.SimpleProperty
import com.google.appinventor.components.common.PropertyTypeConstants
import com.google.appinventor.components.runtime.*
import io.ak1.BubbleTabBarAttributes
import io.ak1.parser.MenuItem
import java.io.FileInputStream
import java.io.InputStream
import io.ak1.BubbleTabBar as TabBar

const val LOG_TAG = "BubbleTabBar"

@Suppress("FunctionName")
class BubbleTabBar(container: ComponentContainer) : AndroidNonvisibleComponent(container.`$form`()) {

    private val context: Context = container.`$context`()
    private var tabBar: TabBar? = null

    private var disabledIconColor: Int = Color.parseColor("#808080")
    private var iconPadding: Float = 5.dp()
    private var horizontalPadding: Float = 15.dp()
    private var verticalPadding: Float = 10.dp()
    private var iconSize: Float = 20.dp()
    private var titleSize: Float = 12.sp()
    private var cornerRadius: Float = 25.dp()
    private var typeFace: Typeface = Typeface.DEFAULT

    private val menuItems: MutableList<MenuItem> = mutableListOf()

    private fun Int.dp(): Float {
        val metrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
    }

    private fun Int.sp(): Float {
        val metrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), metrics)
    }

    private fun getDrawable(form: Form, fileName: String): Drawable? {
        return try {
            val inputStream: InputStream? = getAsset(form, fileName)
            val drawable: Drawable = Drawable.createFromStream(inputStream, null)
            inputStream?.close()
            drawable
        } catch (e: Exception) {
            Log.v(LOG_TAG, "getDrawable : Error = $e")
            null
        }
    }

    private fun getAsset(form: Form, file: String): InputStream? {
        val context = form.`$context`()
        val isDebugMode = form is ReplForm
        return try {
            if (isDebugMode) {
                val path: String = getAssetPath(context, file)
                Log.v(LOG_TAG, "getAsset | Filepath = $path")
                FileInputStream(path)
            } else {
                context.assets.open(file)
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "getAsset | Debug Mode : $isDebugMode | Error : $e")
            null
        }
    }

    private fun getTypeface(context: Context, asset: String): Typeface? {
        return try {
            val path = getAssetPath(context, asset)
            Typeface.createFromFile(path)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "getTypeface | Failed to get typeface from path : $asset with error : $e")
            null
        }
    }

    private fun getAssetPath(context: Context, file: String) = when {
        context.javaClass.name.contains("makeroid") -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.getExternalFilesDir(null).toString() + "/assets/$file"
            } else {
                "/storage/emulated/0/Kodular/assets/$file"
            }
        }
        else -> context.getExternalFilesDir(null).toString() + "/AppInventor/assets/$file"
    }

    @SimpleFunction(description = """
        Add a new menu item.
        
        id - A unique integer that is used to identify each menu item
        title - Title for menu item
        enabled - Set whether the item is enable or not
        color - Background color for menu item
        checked - Set whether the item is checked or not
    """)
    fun Add(id: Int, title: String, icon: String, enabled: Boolean, color: Int, checked: Boolean) {
        val drawable = getDrawable(form, icon)
        menuItems.add(
            MenuItem(id, title, drawable, enabled, color, checked)
        )
    }

    @SimpleFunction(description = """
        Initialize TabBar in a view. Make sure to add all the menu items before initializing.
        
        layout - Any horizontal or vertical arrangement inside which you want to place TabBar
    """)
    fun Initialize(layout: HVArrangement) {
        val viewGroup = layout.view as ViewGroup
        tabBar = TabBar(
            context,
            BubbleTabBarAttributes(
                disabledIconColor,
                iconPadding,
                horizontalPadding,
                verticalPadding,
                iconSize,
                titleSize,
                cornerRadius,
                typeFace
            )
        )
        tabBar?.setMenu(menuItems)
        tabBar?.addBubbleListener { OnSelected(it)}
        viewGroup.addView(
            tabBar,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
    }

    @SimpleFunction(description = """
        Select item at position.
        
        id - A unique integer that is used to identify each menu item
    """)
    fun Select(id: Int) {
        tabBar?.setSelectedWithId(id,true)
    }

    @SimpleEvent(description = """
        Event raised when menu item is selected.
        
        id - A unique integer that is used to identify each menu item
    """)
    fun OnSelected(id: Int) {
        EventDispatcher.dispatchEvent(this, "OnSelected", id)
    }



    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR,
        defaultValue = "&HFF808080"
    )
    @SimpleProperty(description = "Icon color.")
    fun IconColor(color: Int) {
        disabledIconColor = color
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
        defaultValue = "5"
    )
    @SimpleProperty(description = "Icon color.")
    fun IconPadding(padding: Int) {
        iconPadding = padding.dp()
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
        defaultValue = "15"
    )
    @SimpleProperty(description = "Icon color.")
    fun HorizontalPadding(padding: Int) {
        horizontalPadding = padding.dp()
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
        defaultValue = "10"
    )
    @SimpleProperty(description = "Icon color.")
    fun VerticalPadding(padding: Int) {
        verticalPadding = padding.dp()
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
        defaultValue = "20"
    )
    @SimpleProperty(description = "Icon color.")
    fun IconSize(size: Int) {
        iconSize = size.dp()
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
        defaultValue = "12"
    )
    @SimpleProperty(description = "Icon color.")
    fun TitleSize(size: Int) {
        titleSize = size.sp()
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
        defaultValue = "25"
    )
    @SimpleProperty(description = "Icon color.")
    fun CornerRadius(radius: Int) {
        cornerRadius = radius.dp()
    }

    @DesignerProperty(
        editorType = PropertyTypeConstants.PROPERTY_TYPE_ASSET,
    )
    @SimpleProperty(description = "Add a custom font from assets")
    fun Typeface(asset: String) {
        typeFace = getTypeface(context, asset) ?: Typeface.DEFAULT
    }

}
