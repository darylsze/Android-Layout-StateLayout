package darylsze.stateLayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.*
import kotlinx.android.synthetic.main.statelayout_progress_horizontal.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

/**
 * Created by darylsze on 16/5/2017.
 */

/**
 * Add three views in advance, change view upon request
 * User can custom their image, content on each request
 * Background, text color should be set in xml.
 */
class StateLayout : RelativeLayout {

    /**
     * Loading state property
     */
    val loadingContent: String
    val loadingContentTextSize: Float
    val loadingContentTextColor: Int
    val loadingStateProgressBarWidth: Int
    val loadingStateProgressBarHeight: Int
    val loadingStateBackgroundColor: Int

    /**
     * Empty state property
     */
    val emptyTitle: String
    val emptyContent: String
    val emptyImage: Drawable
    val emptyStateImageWidth: Int
    val emptyStateImageHeight: Int
    val emptyStateTitleTextSize: Float
    val emptyStateContentTextSize: Float
    val emptyStateTitleTextColor: Int
    val emptyStateContentTextColor: Int
    val emptyStateBackgroundColor: Int

    /**
     * Error state property
     */
    val errorTitle: String
    val errorContent: String
    val errorImage: Drawable
    val errorStateImageWidth: Int
    val errorStateImageHeight: Int
    val errorStateTitleTextSize: Float
    val errorStateContentTextSize: Float
    val errorStateTitleTextColor: Int
    val errorStateContentTextColor: Int
    val errorStateButtonText: String
    val errorStateButtonTextColor: Int
    val errorStateButtonBackgroundColor: Int
    val errorStateBackgroundColor: Int

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateViews)

        //Loading state attrs
        loadingContent = typedArray.getString(R.styleable.StateViews_loadingContent) ?: context.getString(R.string.loading)
        loadingContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_loadingContentTextSize, 14).toFloat()
        loadingContentTextColor = typedArray.getColor(R.styleable.StateViews_loadingContentTextColor, Color.WHITE)
        loadingStateProgressBarWidth = typedArray.getDimensionPixelSize(R.styleable.StateViews_loadingProgressBarWidth, 108)
        loadingStateProgressBarHeight = typedArray.getDimensionPixelSize(R.styleable.StateViews_loadingProgressBarHeight, 108)
        loadingStateBackgroundColor = typedArray.getColor(R.styleable.StateViews_loadingBackgroundColor, context.getCompatibleColor(R.color.background))

        //Empty state attrs
        emptyImage = typedArray.getDrawable(R.styleable.StateViews_emptyImage) ?: context.resources.getDrawable(R.drawable.ic_email)
        emptyTitle = typedArray.getString(R.styleable.StateViews_emptyTitle) ?: "No item found"
        emptyContent = typedArray.getString(R.styleable.StateViews_emptyContent) ?: "Enjoy free day"
        emptyStateImageWidth = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyImageWidth, 308)
        emptyStateImageHeight = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyImageHeight, 308)
        emptyStateTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyTitleTextSize, 17).toFloat()
        emptyStateContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyContentTextSize, 14).toFloat()
        emptyStateTitleTextColor = typedArray.getColor(R.styleable.StateViews_emptyTitleTextColor, Color.WHITE)
        emptyStateContentTextColor = typedArray.getColor(R.styleable.StateViews_emptyContentTextColor, Color.WHITE)
        emptyStateBackgroundColor = typedArray.getColor(R.styleable.StateViews_emptyBackgroundColor, context.getCompatibleColor(R.color.background))

        //Error state attrs
        errorImage = typedArray.getDrawable(R.styleable.StateViews_errorImage) ?: resources.getDrawable(R.drawable.ic_no_connection)
        errorTitle = typedArray.getString(R.styleable.StateViews_errorTitle) ?: context.getString(R.string.errorTitle)
        errorContent = typedArray.getString(R.styleable.StateViews_errorContent) ?: context.getString(R.string.statelayout_network_failure)
        errorStateImageWidth = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorImageWidth, 308)
        errorStateImageHeight = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorImageHeight, 308)
        errorStateTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorTitleTextSize, 17).toFloat()
        errorStateContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorContentTextSize, 14).toFloat()
        errorStateTitleTextColor = typedArray.getColor(R.styleable.StateViews_errorTitleTextColor, Color.WHITE)
        errorStateContentTextColor = typedArray.getColor(R.styleable.StateViews_errorContentTextColor, Color.WHITE)
        errorStateButtonText = typedArray.getString(R.styleable.StateViews_errorStateButtonText) ?: context.getString(R.string.btn_retry)
        errorStateButtonTextColor = typedArray.getColor(R.styleable.StateViews_errorButtonTextColor, context.getCompatibleColor(R.color.colorPrimary))
        errorStateButtonBackgroundColor = typedArray.getColor(R.styleable.StateViews_errorButtonBackgroundColor, context.getCompatibleColor(R.color.background_button))
        errorStateBackgroundColor = typedArray.getColor(R.styleable.StateViews_errorBackgroundColor, context.getCompatibleColor(R.color.background))

        typedArray.recycle()
    }

    private val emptyView by lazy { makeView(State.EMPTY) }
    private val loadingView by lazy { makeView(State.LOADING) }
    private val errorView by lazy { makeView(State.ERROR) }
    private val progressView by lazy { makeView(State.PROGRESS) }

    private val stateViews: List<Pair<State, View>>
            by lazy {
                listOf(
                        State.EMPTY to emptyView,
                        State.ERROR to errorView,
                        State.LOADING to loadingView,
                        State.PROGRESS to progressView
                )
            }

    /**
     * Show error state view.
     * Must implement retryButtonClick function.
     * [iconDrawable] default: R.drawable.ic_email
     * [title] default: Error
     * [message] default: This error happens because of network connection failure. You may retry later.
     * [errorButtonText] default: retry
     */
    fun showErrorState(
            iconDrawable: Drawable = errorImage,
            title: String = errorTitle,
            message: String = errorContent,
            errorButtonText: String = errorStateButtonText,
            retryButtonClick: (() -> Unit)? = null
    ) {
        showStateInternal(State.ERROR)
        btnErrorRetry.setOnClickListener {
            showLoadingState()
            retryButtonClick?.invoke()
        }
        btnErrorRetry.text = errorButtonText
        lblErrorTitle.text = title
        lblErrorContent.text = message
        imgErrorIcon.setImageDrawable(iconDrawable)
    }

    fun showErrorState(
            throwable: Throwable,
            retryButtonClick: () -> Unit
    ) {
        showErrorState(retryButtonClick = retryButtonClick)
    }

    /**
     * Show loading state view
     * [message] default: Loading...
     */
    fun showLoadingState(message: String) {
        showStateInternal(State.LOADING)
        lblLoadingContent.text = message
    }

    /**
     * Show loading state view
     * [message] default: Loading...
     */
    fun showLoadingState(@StringRes messageRes: Int = R.string.loading) {
        showLoadingState(context.getString(messageRes))
    }

    fun showLoadingProgressState(message: String, newProgress: Int) {
        showStateInternal(State.PROGRESS)
        updateLoadingProgress(newProgress)
        getLoadingProgressView().lblLoading.text = message
    }

    private fun getLoadingProgressView(): View {
        return stateViews.first { it.first == State.PROGRESS }.second
    }

    fun showLoadingProgressState(@StringRes messageRes: Int = R.string.loading, newProgress: Int = 0) {
        showLoadingProgressState(context.getString(messageRes), newProgress)
    }

    /**
     * Show empty state view
     * [iconDrawable] default: ic_email
     * [title] default: No item found
     * [message] default: Enjoy free day
     */
    fun showEmptyState(
            iconDrawable: Drawable = emptyImage,
            title: String = emptyTitle,
            message: String = emptyContent
    ) {
        showStateInternal(State.EMPTY)
        lblEmptyTitle.text = title
        lblEmptyContent.text = message
        imgEmptyIcon.setImageDrawable(iconDrawable)
    }

    /**
     * Hide all state views
     */
    fun dismissAllState() {
        stateViews.forEach { it.second.visibility = View.GONE }
    }

    /**
     * hide error state view
     */
    fun dismissErrorState() {
        errorView.visibility = View.GONE
    }

    /**
     * hide loading state view
     */
    fun dismissLoadingState() {
        loadingView.visibility = View.GONE
    }

    /**
     * hide empty state view
     */
    fun dismissEmptyState() {
        emptyView.visibility = View.GONE
    }


    private fun showStateInternal(toState: State) {
//        dismissAllState()
//        stateViews.firstOrNull { it.first == toState }.apply { this?.second?.show() }
        // confirm state views are ready in layout,
        // otherwise add them all.
        if (!hasStateViewChild()) {
            stateViews.forEach {
                addView(it.second.removeParentView())
            }
        }

//         by controlling state views' visibility
//         to show or dismiss state view.
        stateViews.forEach { (state, view) ->
            if (state != toState)
                view.visibility = View.GONE
            else
                view.visibility = View.VISIBLE
        }
    }

    private fun hasStateViewChild(): Boolean {
        return (0..childCount)
                .map { getChildAt(it) }
                .containsAll(stateViews.map { it.second })
    }

    private fun makeView(state: State): View {
        return when (state) {
            State.EMPTY    -> {
                makeEmptyView()
            }
            State.LOADING  -> {
                makeLoadingView()
            }
            State.ERROR    -> {
                makeErrorView()
            }
            State.PROGRESS -> {
                makeProgressView()
            }
            else           -> throw NotImplementedError("not yet implemented for state $state")
        }
    }

    private lateinit var imgErrorIcon: ImageView
    private lateinit var lblErrorTitle: TextView
    private lateinit var lblErrorContent: TextView
    private lateinit var btnErrorRetry: Button

    private fun makeErrorView(): View {
        return verticalLayout {
            backgroundColor = errorStateBackgroundColor
            lparams(matchParent, matchParent) {
                gravity = Gravity.CENTER_HORIZONTAL
                padding = dip(15)
            }

            // image
            imgErrorIcon = imageView {
                setImageDrawable(errorImage)
                backgroundColor = errorStateBackgroundColor
            }.lparams(errorStateImageWidth, errorStateImageHeight) {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dip(40)
            }

            // title
            lblErrorTitle = textView(errorTitle) {
                textSize = errorStateTitleTextSize
                textColor = errorStateTitleTextColor
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.CENTER_HORIZONTAL
                horizontalMargin = dip(16)
                topMargin = dip(20)
            }

            // content
            lblErrorContent = textView(errorContent) {
                textSize = errorStateContentTextSize
                textColor = errorStateContentTextColor
                gravity = Gravity.CENTER_HORIZONTAL
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.CENTER_HORIZONTAL
                horizontalMargin = dip(56)
                topMargin = dip(10)
                bottomMargin = dip(16)
            }

            // button
            btnErrorRetry = button(errorStateButtonText) {
                setBackgroundColor(errorStateButtonBackgroundColor)
                textColor = errorStateButtonTextColor
            }.lparams {
                gravity = Gravity.CENTER_HORIZONTAL
                padding = dip(15)
            }
        }
    }

    private lateinit var loadingPd: ProgressBar
    //    private lateinit var loadingPdHorizontal: ProgressBar
    private lateinit var lblLoadingContent: TextView

    private fun makeLoadingView(): View {

        return relativeLayout {
            backgroundColor = loadingStateBackgroundColor
            lparams(matchParent, matchParent) {
                padding = dip(35)
            }

            verticalLayout {
                // progress bar
                loadingPd = progressBar {
                }.lparams(wrapContent, wrapContent) {
                    width = loadingStateProgressBarWidth
                    height = loadingStateProgressBarHeight
                    gravity = Gravity.CENTER
                    bottomMargin = dip(20)
                }

                // content
                lblLoadingContent = textView(loadingContent) {
                    textColor = loadingContentTextColor
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }.lparams {
                centerInParent()
            }

        }

    }

    @SuppressLint("RestrictedApi")
    inline fun ViewManager.myHorizontalProgressBar(init: ProgressBar.() -> Unit): ProgressBar {
        return ankoView({ ProgressBar(ContextThemeWrapper(it, R.style.Widget_AppCompat_ProgressBar_Horizontal), null, 0) }, 0) {
            init()
        }
    }

    private fun makeProgressView(): View {
        return context.layoutInflater.inflate(R.layout.statelayout_progress_horizontal, this, false)
    }

    private lateinit var imgEmptyIcon: ImageView
    private lateinit var lblEmptyTitle: TextView
    private lateinit var lblEmptyContent: TextView

    private fun makeEmptyView(): View {
        return verticalLayout {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = 1000f
            } else {
                //todo
            }
            isClickable = true
            backgroundColor = emptyStateBackgroundColor
            gravity = Gravity.CENTER_HORIZONTAL
            padding = dip(15)
            lparams(matchParent, matchParent)

            // icon image
            imgEmptyIcon = imageView {
                setImageDrawable(emptyImage)
            }.lparams {
                gravity = Gravity.CENTER_HORIZONTAL
                width = emptyStateImageWidth
                height = emptyStateImageHeight
                topMargin = dip(40)
            }

            // title
            lblEmptyTitle = textView(emptyTitle) {
                textSize = emptyStateTitleTextSize
                textColor = emptyStateTitleTextColor
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dip(20)
                horizontalMargin = dip(16)
            }

            // content
            lblEmptyContent = textView(emptyContent) {
                textSize = emptyStateContentTextSize
                textColor = emptyStateContentTextColor
                gravity = Gravity.CENTER_HORIZONTAL
                setLineSpacing(5.6f, 1f)
            }.lparams(wrapContent, wrapContent) {
                horizontalMargin = dip(56)
                topMargin = dip(20)
                bottomMargin = dip(16)
            }
        }
    }

    fun updateLoadingProgress(progress: Int) {
        stateViews
                .firstOrNull { (state, view) -> state == State.PROGRESS }
                ?.apply { this.second.pbHorizontal.progress = progress }
    }
}

private fun Context.getCompatibleColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}


private fun View.removeParentView(): View {
    if (parent != null)
        (parent as ViewGroup).removeView(this)
    return this
}


enum class State {
    LOADING, EMPTY, ERROR, CONTENT, PROGRESS
}
