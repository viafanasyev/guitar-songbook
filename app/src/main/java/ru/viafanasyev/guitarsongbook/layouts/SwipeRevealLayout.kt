/**
 The MIT License (MIT)

 Copyright (c) 2016 Chau Thai

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package ru.viafanasyev.guitarsongbook.layouts

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import ru.viafanasyev.guitarsongbook.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * This is a simplified version of a SwipeRevealLayout by chthai64
 * @author Chau Thai
 * @see <a href="https://github.com/chthai64/SwipeRevealLayout">GitHub Repository</a>
 */
class SwipeRevealLayout : ViewGroup {
    private lateinit var mainView: View
    private lateinit var secondaryView: View

    private val rectMainClose: Rect = Rect()
    private val rectMainOpen: Rect = Rect()

    private val rectSecClose: Rect = Rect()
    private val rectSecOpen: Rect = Rect()

    @Volatile
    private var isScrolling = false

    private var isOpen = false

    /**
     * True if the drag/swipe motion is currently locked.
     */
    @Volatile
    var isDragLocked = false

    private val minFlingVelocity: Int
    private val dragEdge: Int
    private val dragHelper: ViewDragHelper
    private val gestureDetector: GestureDetectorCompat

    private var dragDist = 0f
    private var prevX = -1f

    var onOpen: (() -> Unit)? = null
    var onClose: (() -> Unit)? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        dragEdge = if (attrs != null) {
            context.theme
                .obtainStyledAttributes(attrs, R.styleable.SwipeRevealLayout, 0, 0)
                .getInteger(R.styleable.SwipeRevealLayout_dragFromEdge, DRAG_EDGE_RIGHT)
        } else {
            DRAG_EDGE_RIGHT
        }
        minFlingVelocity = DEFAULT_MIN_FLING_VELOCITY
        dragHelper = ViewDragHelper.create(this, 1.0f, createDragHelperCallback())
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL)
        gestureDetector = GestureDetectorCompat(context, createGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        dragHelper.processTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (isDragLocked) {
            return super.onInterceptTouchEvent(ev)
        }
        dragHelper.processTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)
        accumulateDragDist(ev)

        val couldBecomeClick = couldBecomeClick(ev)
        val settling = dragHelper.viewDragState == ViewDragHelper.STATE_SETTLING
        val idleAfterScrolled = (dragHelper.viewDragState == ViewDragHelper.STATE_IDLE && isScrolling)

        // must be placed as the last statement
        prevX = ev.x

        // return true => intercept, cannot trigger onClick event
        return !couldBecomeClick && (settling || idleAfterScrolled)
    }

    private fun accumulateDragDist(ev: MotionEvent) {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            dragDist = 0f
            return
        }
        val dragged = abs(ev.x - prevX)
        dragDist += dragged
    }

    override fun onFinishInflate() {
        check(childCount == 2) { "Layout should have exactly two children" }

        super.onFinishInflate()

        mainView = getChildAt(0)
        secondaryView = getChildAt(1)
    }

    /**
     * {@inheritDoc}
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        check(childCount == 2) { "Layout should have exactly two children" }

        childLayout(mainView, r, l, b, t)
        childLayout(secondaryView, r, l, b, t)

        when (dragEdge) {
            DRAG_EDGE_LEFT -> secondaryView.offsetLeftAndRight(-secondaryView.width)
            DRAG_EDGE_RIGHT -> secondaryView.offsetLeftAndRight(secondaryView.width)
            else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
        }

        initRects()

        if (isOpen) {
            drawOpen(false)
        } else {
            drawClose(false)
        }
    }

    private fun childLayout(child: View, r: Int, l: Int, b: Int, t: Int) {
        val minLeft = paddingLeft
        val maxRight = (r - paddingRight - l).coerceAtLeast(0)
        val minTop = paddingTop
        val maxBottom = (b - paddingBottom - t).coerceAtLeast(0)

        val measuredChildHeight: Int
        val measuredChildWidth: Int
        val childParams = child.layoutParams
        if (childParams.height == LayoutParams.MATCH_PARENT) {
            measuredChildHeight = maxBottom - minTop
            childParams.height = measuredChildHeight
        } else {
            measuredChildHeight = child.measuredHeight
        }
        if (childParams.width == LayoutParams.MATCH_PARENT) {
            measuredChildWidth = maxRight - minLeft
            childParams.width = measuredChildWidth
        } else {
            measuredChildWidth = child.measuredWidth
        }

        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        when (dragEdge) {
            DRAG_EDGE_RIGHT -> {
                left = (r - measuredChildWidth - paddingRight - l).coerceAtLeast(minLeft)
                top = paddingTop.coerceAtMost(maxBottom)
                right = (r - paddingRight - l).coerceAtLeast(minLeft)
                bottom = (measuredChildHeight + paddingTop).coerceAtMost(maxBottom)
            }
            DRAG_EDGE_LEFT -> {
                left = paddingLeft.coerceAtMost(maxRight)
                top = paddingTop.coerceAtMost(maxBottom)
                right = (measuredChildWidth + paddingLeft).coerceAtMost(maxRight)
                bottom = (measuredChildHeight + paddingTop).coerceAtMost(maxBottom)
            }
            else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
        }

        child.layout(left, top, right, bottom)
    }

    /**
     * {@inheritDoc}
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        check(childCount == 2) { "Layout must have exactly two children" }

        var (desiredWidth, desiredHeight) = getLargestChildSizes(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        // create new measure spec using the largest child width
        val newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(desiredWidth, widthMode)
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(desiredHeight, heightMode)
        val measuredWidth = MeasureSpec.getSize(newWidthMeasureSpec)
        val measuredHeight = MeasureSpec.getSize(newHeightMeasureSpec)

        adjustChildrenMinimumSizes(measuredHeight, measuredWidth)

        val (newDesiredWidth, newDesiredHeight) = getLargestChildSizes(newWidthMeasureSpec, newHeightMeasureSpec)
        desiredWidth = max(newDesiredWidth, desiredWidth)
        desiredHeight = max(newDesiredHeight, desiredHeight)

        desiredWidth += paddingLeft + paddingRight
        desiredHeight += paddingTop + paddingBottom

        if (widthMode == MeasureSpec.EXACTLY || layoutParams.width == LayoutParams.MATCH_PARENT) {
            desiredWidth = measuredWidth
        } else if (widthMode == MeasureSpec.AT_MOST) {
            desiredWidth = desiredWidth.coerceAtMost(measuredWidth)
        }

        if (heightMode == MeasureSpec.EXACTLY || layoutParams.height == LayoutParams.MATCH_PARENT) {
            desiredHeight = measuredHeight
        } else if (heightMode == MeasureSpec.AT_MOST) {
            desiredHeight = desiredHeight.coerceAtMost(measuredHeight)
        }

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    private fun adjustChildrenMinimumSizes(measuredHeight: Int, measuredWidth: Int) {
        adjustChildMinimumSizes(mainView, measuredHeight, measuredWidth)
        adjustChildMinimumSizes(secondaryView, measuredHeight, measuredWidth)
    }

    private fun adjustChildMinimumSizes(child: View, measuredHeight: Int, measuredWidth: Int) {
        val childParams = child.layoutParams
        if (childParams.height == LayoutParams.MATCH_PARENT) {
            child.minimumHeight = measuredHeight
        }
        if (childParams.width == LayoutParams.MATCH_PARENT) {
            child.minimumWidth = measuredWidth
        }
    }

    private fun getLargestChildSizes(widthMeasureSpec: Int, heightMeasureSpec: Int): Pair<Int, Int> {
        check(childCount == 2) { "Layout must have exactly two children" }

        measureChildren(widthMeasureSpec, heightMeasureSpec)
        val maxWidth = max(mainView.measuredWidth, secondaryView.measuredWidth)
        val maxHeight = max(mainView.measuredHeight, secondaryView.measuredHeight)
        return Pair(maxWidth, maxHeight)
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    /**
     * Open the panel to show the secondary view
     */
    fun open(animation: Boolean) {
        if (isOpen) {
            return
        }

        isOpen = true
        drawOpen(animation)
        onOpen?.invoke()
    }

    private fun drawOpen(animation: Boolean) {
        if (animation) {
            dragHelper.smoothSlideViewTo(mainView, rectMainOpen.left, rectMainOpen.top)
        } else {
            dragHelper.abort()
            mainView.layout(
                rectMainOpen.left,
                rectMainOpen.top,
                rectMainOpen.right,
                rectMainOpen.bottom
            )
            secondaryView.layout(
                rectSecOpen.left,
                rectSecOpen.top,
                rectSecOpen.right,
                rectSecOpen.bottom
            )
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**
     * Close the panel to hide the secondary view
     */
    fun close(animation: Boolean) {
        if (!isOpen) {
            return
        }

        isOpen = false
        drawClose(animation)
        onClose?.invoke()
    }

    private fun drawClose(animation: Boolean) {
        if (animation) {
            dragHelper.smoothSlideViewTo(mainView, rectMainClose.left, rectMainClose.top)
        } else {
            dragHelper.abort()
            mainView.layout(
                rectMainClose.left,
                rectMainClose.top,
                rectMainClose.right,
                rectMainClose.bottom
            )
            secondaryView.layout(
                rectSecClose.left,
                rectSecClose.top,
                rectSecClose.right,
                rectSecClose.bottom
            )
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun initRects() {
        check(childCount == 2) { "Layout should have exactly two children" }

        initMainRect()
        initSecRect()
    }

    private fun initMainRect() {
        rectMainClose.set(
            mainView.left,
            mainView.top,
            mainView.right,
            mainView.bottom
        )

        val mainOpenLeft = when (dragEdge) {
            DRAG_EDGE_LEFT -> mainView.left + secondaryView.width
            DRAG_EDGE_RIGHT -> mainView.left - secondaryView.width
            else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
        }

        rectMainOpen.set(
            mainOpenLeft,
            mainView.top,
            mainOpenLeft + mainView.width,
            mainView.bottom
        )
    }

    private fun initSecRect() {
        rectSecClose.set(
            secondaryView.left,
            secondaryView.top,
            secondaryView.right,
            secondaryView.bottom
        )

        val secOpenLeft = when (dragEdge) {
            DRAG_EDGE_LEFT -> secondaryView.left + secondaryView.width
            DRAG_EDGE_RIGHT -> secondaryView.left - secondaryView.width
            else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
        }

        rectSecOpen.set(
            secOpenLeft,
            secondaryView.top,
            secOpenLeft + secondaryView.width,
            secondaryView.bottom
        )
    }

    private fun couldBecomeClick(ev: MotionEvent): Boolean {
        return isInMainView(ev) && !shouldInitiateDrag()
    }

    private fun isInMainView(ev: MotionEvent): Boolean {
        check(childCount == 2) { "Layout should have exactly two children" }

        val x = ev.x
        val y = ev.y
        val withinVertical = mainView.top <= y && y <= mainView.bottom
        val withinHorizontal = mainView.left <= x && x <= mainView.right
        return withinVertical && withinHorizontal
    }

    private fun shouldInitiateDrag(): Boolean {
        val minDistToInitiateDrag = dragHelper.touchSlop
        return dragDist >= minDistToInitiateDrag
    }

    private fun createGestureListener(): GestureDetector.OnGestureListener = object : SimpleOnGestureListener() {
            var hasDisallowed = false
            override fun onDown(e: MotionEvent): Boolean {
                isScrolling = false
                hasDisallowed = false
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                isScrolling = true
                return false
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                isScrolling = true
                if (parent != null) {
                    val shouldDisallow: Boolean
                    if (!hasDisallowed) {
                        shouldDisallow = distToClosestEdge > 0
                        if (shouldDisallow) {
                            hasDisallowed = true
                        }
                    } else {
                        shouldDisallow = true
                    }

                    // disallow parent to intercept touch event so that the layout will work
                    // properly on RecyclerView or view that handles scroll gesture.
                    parent.requestDisallowInterceptTouchEvent(shouldDisallow)
                }
                return false
            }
        }

    private val distToClosestEdge: Int
        get() {
            when (dragEdge) {
                DRAG_EDGE_LEFT -> {
                    val pivotRight = rectMainClose.left + secondaryView.width
                    return min(
                        mainView.left - rectMainClose.left,
                        pivotRight - mainView.left
                    )
                }
                DRAG_EDGE_RIGHT -> {
                    val pivotLeft = rectMainClose.right - secondaryView.width
                    return min(
                        rectMainClose.right - mainView.right,
                        mainView.right - pivotLeft
                    )
                }
                else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
            }
        }

    private val halfwayPivotHorizontal: Int
        get() {
            return when (dragEdge) {
                DRAG_EDGE_LEFT -> rectMainClose.left + secondaryView.width / 2
                DRAG_EDGE_RIGHT -> rectMainClose.right - secondaryView.width / 2
                else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
            }
        }

    private fun createDragHelperCallback() = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (!isDragLocked) {
                dragHelper.captureChildView(mainView, pointerId)
            }
            return false
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return when (dragEdge) {
                DRAG_EDGE_RIGHT -> max(
                    min(left, rectMainClose.left),
                    rectMainClose.left - secondaryView.width
                )
                DRAG_EDGE_LEFT -> max(
                    min(left, rectMainClose.left + secondaryView.width),
                    rectMainClose.left
                )
                else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val velRightExceeded = pxToDp(xvel.toInt()) >= minFlingVelocity
            val velLeftExceeded = pxToDp(xvel.toInt()) <= -minFlingVelocity
            val pivotHorizontal = halfwayPivotHorizontal
            when (dragEdge) {
                DRAG_EDGE_RIGHT -> {
                    if (velRightExceeded) {
                        close(true)
                    } else if (velLeftExceeded) {
                        open(true)
                    } else if (mainView.right < pivotHorizontal) {
                        open(true)
                    } else {
                        close(true)
                    }
                }
                DRAG_EDGE_LEFT -> {
                    if (velRightExceeded) {
                        open(true)
                    } else if (velLeftExceeded) {
                        close(true)
                    } else if (mainView.left < pivotHorizontal) {
                        close(true)
                    } else {
                        open(true)
                    }
                }
                else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
            }
        }

        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            super.onEdgeDragStarted(edgeFlags, pointerId)
            if (isDragLocked) {
                return
            }
            val edgeStartLeft = (dragEdge == DRAG_EDGE_RIGHT && edgeFlags == ViewDragHelper.EDGE_LEFT)
            val edgeStartRight = (dragEdge == DRAG_EDGE_LEFT && edgeFlags == ViewDragHelper.EDGE_RIGHT)
            if (edgeStartLeft || edgeStartRight) {
                dragHelper.captureChildView(mainView, pointerId)
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, @Px dx: Int, @Px dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            when (dragEdge) {
                DRAG_EDGE_LEFT, DRAG_EDGE_RIGHT -> secondaryView.offsetLeftAndRight(dx)
                else -> throw UnsupportedOperationException("Unknown drag edge $dragEdge")
            }
            ViewCompat.postInvalidateOnAnimation(this@SwipeRevealLayout)
        }
    }

    private fun pxToDp(px: Int): Int {
        return (px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    companion object {
        private const val DEFAULT_MIN_FLING_VELOCITY = 300 // dp per second
        const val DRAG_EDGE_LEFT = 1
        const val DRAG_EDGE_RIGHT = 2
    }
}