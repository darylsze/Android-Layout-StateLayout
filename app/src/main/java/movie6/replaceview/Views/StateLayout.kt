package movie6.replaceview.Views

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import movie6.replaceview.R
import org.jetbrains.anko.*


/**
 * Created by windsze on 16/5/2017.
 * HKMovie. GT.
 */

val TAG = "StateLayout"

/**
 * Add three views in advance, change view upon request
 * User can custom their image, content on each request
 * Background, text color should be set in xml.
 */
class StateLayout : RelativeLayout {

    /**
     * Loading state property
     */
    private val loadingContent: String
    private val loadingContentTextSize: Float
    private val loadingContentTextColor: Int
    private val loadingStateProgressBarWidth: Int
    private val loadingStateProgressBarHeight: Int
    private val loadingStateProgressBarColor: Int
    private val loadingStateBackgroundColor: Int

    /**
     * Empty state property
     */
    private val emptyTitle: String
    private val emptyContent: String
    private val emptyImage: Drawable
    private val emptyStateImageWidth: Int
    private val emptyStateImageHeight: Int
    private val emptyStateTitleTextSize: Float
    private val emptyStateContentTextSize: Float
    private val emptyStateTitleTextColor: Int
    private val emptyStateContentTextColor: Int
    private val emptyStateBackgroundColor: Int

    /**
     * Error state property
     */
    private val errorTitle: String
    private val errorContent: String
    private val errorImage: Drawable
    private val errorStateImageWidth: Int
    private val errorStateImageHeight: Int
    private val errorStateTitleTextSize: Float
    private val errorStateContentTextSize: Float
    private val errorStateTitleTextColor: Int
    private val errorStateContentTextColor: Int
    private val errorStateButtonText: String
    private val errorStateButtonTextColor: Int
    private val errorStateButtonBackgroundColor: Int
    private val errorStateBackgroundColor: Int

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StateViews)

        //Loading state attrs
        loadingContent = typedArray.getString(R.styleable.StateViews_loadingContent) ?: "Loading..."
        loadingContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_loadingContentTextSize, 14).toFloat()
        loadingContentTextColor = typedArray.getColor(R.styleable.StateViews_loadingContentTextColor, Color.BLACK)
        loadingStateProgressBarWidth = typedArray.getDimensionPixelSize(R.styleable.StateViews_loadingProgressBarWidth, 108)
        loadingStateProgressBarHeight = typedArray.getDimensionPixelSize(R.styleable.StateViews_loadingProgressBarHeight, 108)
        loadingStateProgressBarColor = typedArray.getColor(R.styleable.StateViews_loadingProgressBarColor, Color.RED)
        loadingStateBackgroundColor = typedArray.getColor(R.styleable.StateViews_loadingBackgroundColor, Color.BLUE)

        //Empty state attrs
        emptyImage = typedArray.getDrawable(R.styleable.StateViews_emptyImage) ?: context.resources.getDrawable(R.drawable.ic_email)
        emptyTitle = typedArray.getString(R.styleable.StateViews_emptyTitle) ?: "No item found"
        emptyContent = typedArray.getString(R.styleable.StateViews_emptyContent) ?: "Enjoy free day"
        emptyStateImageWidth = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyImageWidth, 308)
        emptyStateImageHeight = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyImageHeight, 308)
        emptyStateTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyTitleTextSize, 17).toFloat()
        emptyStateContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_emptyContentTextSize, 14).toFloat()
        emptyStateTitleTextColor = typedArray.getColor(R.styleable.StateViews_emptyTitleTextColor, Color.BLACK)
        emptyStateContentTextColor = typedArray.getColor(R.styleable.StateViews_emptyContentTextColor, Color.BLACK)
        emptyStateBackgroundColor = typedArray.getColor(R.styleable.StateViews_emptyBackgroundColor, Color.BLUE)

        //Error state attrs
        errorImage = typedArray.getDrawable(R.styleable.StateViews_errorImage) ?: resources.getDrawable(R.drawable.ic_no_connection)
        errorTitle = typedArray.getString(R.styleable.StateViews_errorTitle) ?: "Error"
        errorContent = typedArray.getString(R.styleable.StateViews_errorContent) ?: "This error happens because of network connection failure. You may retry later."
        errorStateImageWidth = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorImageWidth, 308)
        errorStateImageHeight = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorImageHeight, 308)
        errorStateTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorTitleTextSize, 17).toFloat()
        errorStateContentTextSize = typedArray.getDimensionPixelSize(R.styleable.StateViews_errorContentTextSize, 14).toFloat()
        errorStateTitleTextColor = typedArray.getColor(R.styleable.StateViews_errorTitleTextColor, Color.BLACK)
        errorStateContentTextColor = typedArray.getColor(R.styleable.StateViews_errorContentTextColor, Color.BLACK)
        errorStateButtonText = typedArray.getString(R.styleable.StateViews_errorStateButtonText) ?: "Retry"
        errorStateButtonTextColor = typedArray.getColor(R.styleable.StateViews_errorButtonTextColor, Color.BLACK)
        errorStateButtonBackgroundColor = typedArray.getColor(R.styleable.StateViews_errorButtonBackgroundColor, Color.WHITE)
        errorStateBackgroundColor = typedArray.getColor(R.styleable.StateViews_errorBackgroundColor, Color.BLUE)

        typedArray.recycle()
    }

    private val emptyView by lazy { makeView(State.EMPTY) }
    private val loadingView by lazy { makeView(State.LOADING) }
    private val errorView by lazy { makeView(State.ERROR) }

    private val stateViews by lazy {
        listOf(
                State.EMPTY to emptyView,
                State.ERROR to errorView,
                State.LOADING to loadingView
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
    fun showErrorState(iconDrawable: Drawable = errorImage,
                       title: String = errorTitle,
                       message: String = errorContent,
                       errorButtonText: String = errorStateButtonText,
                       retryButtonClick: () -> Unit
    ) {
        showStateInternal(State.ERROR)
        btnErrorRetry.setOnClickListener {
            dismissErrorState()
            retryButtonClick.invoke()
        }
        btnErrorRetry.text = errorButtonText
        lblErrorTitle.text = title
        lblErrorContent.text = message
        imgErrorIcon.setImageDrawable(iconDrawable)

    }

    /**
     * Show loading state view
     * [message] default: Loading...
     */
    fun showLoadingState(message: String = loadingContent) {
        showStateInternal(State.LOADING)
        lblLoadingContent.text = message
    }

    /**
     * Show empty state view
     * [iconDrawable] default: ic_email
     * [title] default: No item found
     * [message] default: Enjoy free day
     */
    fun showEmptyState(iconDrawable: Drawable = emptyImage,
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
        // confirm state views are ready in layout,
        // otherwise add them all.
        if (!hasStateViewChild()) {
            stateViews.forEach {
                addView(it.second.removeParentView())
            }
        }

        // by controlling state views' visibility
        // to show or dismiss state view.
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
        when (state) {
            State.EMPTY -> {
                return makeEmptyView()
            }
            State.LOADING -> {
                return makeLoadingView()
            }
            State.ERROR -> {
                return makeErrorView()
            }
            else -> throw Error()
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
            }
        }

    }

    private lateinit var loadingPd: ProgressBar
    private lateinit var lblLoadingContent: TextView

    private fun makeLoadingView(): View {
        return verticalLayout {
            backgroundColor = loadingStateBackgroundColor
            lparams(matchParent, matchParent) {
                padding = dip(15)
                topMargin = dip(40)
            }

            // progress bar
            loadingPd = progressBar {
                indeterminateDrawable.setColorFilter(loadingStateProgressBarColor, PorterDuff.Mode.SRC_IN)
            }.lparams(wrapContent, wrapContent) {
                width = loadingStateProgressBarWidth
                height = loadingStateProgressBarHeight
                gravity = Gravity.CENTER
            }

            // content
            lblLoadingContent = textView(loadingContent) {
                textColor = loadingContentTextColor
            }.lparams(wrapContent, wrapContent) {
                topMargin = dip(20)
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

    }

    private lateinit var imgEmptyIcon: ImageView
    private lateinit var lblEmptyTitle: TextView
    private lateinit var lblEmptyContent: TextView

    private fun makeEmptyView(): View {
        return verticalLayout {
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
                topMargin = dip(30)
            }

            // title
            lblEmptyTitle = textView(emptyTitle) {
                textSize = emptyStateTitleTextSize
                textColor = emptyStateTitleTextColor
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dip(10)
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
                topMargin = dip(10)
                bottomMargin = dip(16)
            }
        }
    }
}


private fun View.removeParentView(): View {
    if (parent != null)
        (parent as ViewGroup).removeView(this)
    return this
}


enum class State {
    LOADING, EMPTY, ERROR, CONTENT
}
