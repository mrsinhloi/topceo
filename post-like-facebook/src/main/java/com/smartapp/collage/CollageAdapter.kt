package com.smartapp.collage

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smartapp.post_like_facebook.R
import kotlinx.android.synthetic.main.collage_five_with_one_horizontal.view.*
import kotlinx.android.synthetic.main.collage_five_with_one_vertical.view.*
import kotlinx.android.synthetic.main.collage_four_with_one_horizontal.view.*
import kotlinx.android.synthetic.main.collage_four_with_one_square.view.*
import kotlinx.android.synthetic.main.collage_four_with_one_vertical.view.*
import kotlinx.android.synthetic.main.collage_one_horizontal.view.*
import kotlinx.android.synthetic.main.collage_three_with_one_horizontal.view.*
import kotlinx.android.synthetic.main.collage_three_with_one_vertical.view.*
import kotlinx.android.synthetic.main.collage_two_horizontal.view.*
import kotlinx.android.synthetic.main.collage_two_horizontal_and_vertical.view.*
import kotlinx.android.synthetic.main.collage_two_vertical.view.*
import kotlinx.android.synthetic.main.collage_two_vertical_and_horizontal.view.*

class CollageAdapter(images: List<Bitmap>) : RecyclerView.Adapter<CollageAdapter.BasePhotoGridHolder>() {

    private object ViewTypeDelegate {

        internal const val ONE_IMAGE = 1

        internal const val TWO_HORIZONTAL_IMAGES = 3
        internal const val TWO_VERTICAL_IMAGES = 4
        internal const val TWO_VERTICAL_HORIZONTAL_IMAGES = 5
        internal const val TWO_HORIZONTAL_VERTICAL_IMAGES = 6

        internal const val THREE_WITH_ONE_HORIZONTAL_IMAGES = 7
        internal const val THREE_WITH_ONE_VERTICAL_IMAGES = 8

        internal const val FOUR_WITH_ONE_HORIZONTAL_IMAGES = 9
        internal const val FOUR_WITH_ONE_VERTICAL_IMAGES = 10
        internal const val FOUR_WITH_ONE_SQUARE_IMAGES = 11

        internal const val FIVE_WITH_ONE_VERTICAL_IMAGES = 12
        internal const val FIVE_WITH_ONE_HORIZONTAL_IMAGES = 13



        internal fun getViewTypeForOneImage(bitmap: Bitmap) = ONE_IMAGE

        internal fun getViewTypeForTwoImages(images: List<Bitmap>): Int {
            return if(images.first().isSquare() && images.second().isSquare()){
                TWO_HORIZONTAL_VERTICAL_IMAGES
            }else if (images.first().isVertical() && images.second().isVertical()) {
                TWO_VERTICAL_IMAGES
            } else if (images.first().isHorizontal() && images.second().isHorizontal()) {
                TWO_HORIZONTAL_IMAGES
            } else if (images.first().isHorizontal() && images.second().isVertical()) {
                TWO_HORIZONTAL_VERTICAL_IMAGES
            } else if (images.first().isVertical() && images.second().isHorizontal()) {
                TWO_VERTICAL_HORIZONTAL_IMAGES
            } else {
//                throw RuntimeException("No possible combination")
                TWO_HORIZONTAL_VERTICAL_IMAGES
            }
        }

        internal fun getViewTypeForThreeImages(bitmap: Bitmap) =
                if (bitmap.isVertical()) THREE_WITH_ONE_VERTICAL_IMAGES else THREE_WITH_ONE_HORIZONTAL_IMAGES

        internal fun getViewTypeForFourImages(bitmap: Bitmap) =
                if (bitmap.isSquare())
                    FOUR_WITH_ONE_SQUARE_IMAGES
                else if (bitmap.isVertical())
                    FOUR_WITH_ONE_VERTICAL_IMAGES
                else
                    FOUR_WITH_ONE_HORIZONTAL_IMAGES

        internal fun getViewTypeForFiveImages(bitmap: Bitmap) =
                if (bitmap.isVertical()) FIVE_WITH_ONE_VERTICAL_IMAGES else FIVE_WITH_ONE_HORIZONTAL_IMAGES

    }

    private val loadedImages: MutableList<List<Bitmap>> by lazy { mutableListOf(images) }

    override fun getItemViewType(position: Int): Int {
        return loadedImages[position].sortedByDescending { it.height * it.width }.let { images ->
            images.first().let { bitmap ->
                when (images.size) {
                    1 -> ViewTypeDelegate.getViewTypeForOneImage(bitmap)
                    2 -> ViewTypeDelegate.getViewTypeForTwoImages(images)
                    3 -> ViewTypeDelegate.getViewTypeForThreeImages(bitmap)
                    4 -> ViewTypeDelegate.getViewTypeForFourImages(bitmap)
                    else -> ViewTypeDelegate.getViewTypeForFiveImages(bitmap)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePhotoGridHolder {
        return when (viewType) {
            /*ViewTypeDelegate.ONE_IMAGE -> {
                OneVerticalImageHolder(parent.inflate(R.layout.collage_one_horizontal))
            }*/
            ViewTypeDelegate.ONE_IMAGE -> {
                OneHorizontalImageHolder(parent.inflate(R.layout.collage_one_horizontal))
            }

            ViewTypeDelegate.TWO_VERTICAL_IMAGES -> {
                TwoVerticalImagesHolder(parent.inflate(R.layout.collage_two_vertical))
            }
            ViewTypeDelegate.TWO_HORIZONTAL_IMAGES -> {
                TwoHorizontalImagesHolder(parent.inflate(R.layout.collage_two_horizontal))
            }
            ViewTypeDelegate.TWO_VERTICAL_HORIZONTAL_IMAGES -> {
                VerticalAndHorizontalImagesHolder(parent.inflate(R.layout.collage_two_vertical_and_horizontal))
            }
            ViewTypeDelegate.TWO_HORIZONTAL_VERTICAL_IMAGES -> {
                HorizontalAndVerticalImagesHolder(parent.inflate(R.layout.collage_two_horizontal_and_vertical))
            }

            ViewTypeDelegate.THREE_WITH_ONE_VERTICAL_IMAGES -> {
                ThreeWithOneVerticalImageHolder(parent.inflate(R.layout.collage_three_with_one_vertical))
            }
            ViewTypeDelegate.THREE_WITH_ONE_HORIZONTAL_IMAGES -> {
                ThreeWithOneHorizontalImageHolder(parent.inflate(R.layout.collage_three_with_one_horizontal))
            }

            ViewTypeDelegate.FOUR_WITH_ONE_VERTICAL_IMAGES -> {
                FourWithOneVerticalImageHolder(parent.inflate(R.layout.collage_four_with_one_vertical))
            }
            ViewTypeDelegate.FOUR_WITH_ONE_HORIZONTAL_IMAGES -> {
                FourWithOneHorizontalImageHolder(parent.inflate(R.layout.collage_four_with_one_horizontal))
            }
            ViewTypeDelegate.FOUR_WITH_ONE_SQUARE_IMAGES -> {
                FourWithOneSquareImageHolder(parent.inflate(R.layout.collage_four_with_one_square))
            }


            ViewTypeDelegate.FIVE_WITH_ONE_VERTICAL_IMAGES -> {
                FiveWithOneVerticalImageHolder(parent.inflate(R.layout.collage_five_with_one_vertical))
            }
            ViewTypeDelegate.FIVE_WITH_ONE_HORIZONTAL_IMAGES -> {
                FiveWithOneHorizontalImageHolder(parent.inflate(R.layout.collage_five_with_one_horizontal))
            }

            else -> {
                throw RuntimeException("No such viewType: $viewType")
            }
        }
    }

    override fun getItemCount(): Int {
        return loadedImages.size
    }

    override fun onBindViewHolder(holder: BasePhotoGridHolder, position: Int) {
        holder.bind(loadedImages[position])
    }


    operator fun plusAssign(post: List<Bitmap>) {
        loadedImages += post
        notifyItemInserted(loadedImages.lastIndex)
    }

    fun resetImages(images: List<Bitmap>) {
        loadedImages.clear()
        loadedImages += images
        notifyDataSetChanged()
    }

    inner class FiveWithOneVerticalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                fivev_image1.setImageBitmap(images[0])
                fivev_image2.setImageBitmap(images[1])
                fivev_image3.setImageBitmap(images[2])
                fivev_image4.setImageBitmap(images[3])
                fivev_image5.setImageBitmap(images[4])


                if (images.size > 5) {
                    fivev_load_more.text = "+${images.size-5}"
                    fivev_load_more.visibility = View.VISIBLE
                } else {
                    fivev_load_more.visibility = View.GONE
                }
            }
        }
    }
    inner class FiveWithOneHorizontalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                fiveh_image1.setImageBitmap(images[0])
                fiveh_image2.setImageBitmap(images[1])
                fiveh_image3.setImageBitmap(images[2])
                fiveh_image4.setImageBitmap(images[3])
                fiveh_image5.setImageBitmap(images[4])


                if (images.size > 5) {
                    fiveh_load_more.text = "+${images.size - 5}"
                    fiveh_load_more.visibility = View.VISIBLE
                } else {
                    fiveh_load_more.visibility = View.GONE
                }
            }
        }
    }



    inner class FourWithOneSquareImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                fwos_image1.setImageBitmap(images[0])
                fwos_image2.setImageBitmap(images[1])
                fwos_image3.setImageBitmap(images[2])
                fwos_image4.setImageBitmap(images[3])

                if (images.size > 4) {
                    fwos_load_more.text = "+${images.size - 4}"
                    fwos_load_more.visibility = View.VISIBLE
                } else {
                    fwos_load_more.visibility = View.GONE
                }
            }
        }
    }

    inner class FourWithOneVerticalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                fwov_image1.setImageBitmap(images[0])
                fwov_image2.setImageBitmap(images[1])
                fwov_image3.setImageBitmap(images[2])
                fwov_image4.setImageBitmap(images[3])

                if (images.size > 4) {
                    fwov_load_more.text = "+${images.size - 4}"
                    fwov_load_more.visibility = View.VISIBLE
                } else {
                    fwov_load_more.visibility = View.GONE
                }
            }
        }
    }


    inner class FourWithOneHorizontalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                fwoh_image1.setImageBitmap(images[0])
                fwoh_image2.setImageBitmap(images[1])
                fwoh_image3.setImageBitmap(images[2])
                fwoh_image4.setImageBitmap(images[3])

                if (images.size > 4) {
                    fwoh_load_more.text = "+${images.size - 4}"
                    fwoh_load_more.visibility = View.VISIBLE
                } else {
                    fwoh_load_more.visibility = View.GONE
                }
            }
        }
    }


    inner class ThreeWithOneVerticalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                twov_image1.setImageBitmap(images[0])
                twov_image2.setImageBitmap(images[1])
                twov_image3.setImageBitmap(images[2])
            }
        }
    }


    inner class ThreeWithOneHorizontalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                twoh_image1.setImageBitmap(images[0])
                twoh_image2.setImageBitmap(images[1])
                twoh_image3.setImageBitmap(images[2])
            }
        }
    }


    inner class VerticalAndHorizontalImagesHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                vah_image1.setImageBitmap(images[0])
                vah_image2.setImageBitmap(images[1])
            }
        }
    }


    inner class HorizontalAndVerticalImagesHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                hav_image1.setImageBitmap(images[0])
                hav_image2.setImageBitmap(images[1])
            }
        }
    }


    inner class TwoVerticalImagesHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                tv_image1.setImageBitmap(images[0])
                tv_image2.setImageBitmap(images[1])
            }
        }
    }


    inner class TwoHorizontalImagesHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                th_image1.setImageBitmap(images[0])
                th_image2.setImageBitmap(images[1])
            }
        }
    }



    /*inner class OneVerticalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                os_image1.setImageBitmap(images[0])
            }
        }
    }*/


    inner class OneHorizontalImageHolder(v: View) : BasePhotoGridHolder(v) {
        override fun bind(images: List<Bitmap>) {
            with(itemView) {
                oh_image1.setImageBitmap(images[0])
            }
        }
    }


    abstract inner class BasePhotoGridHolder(v: View) : RecyclerView.ViewHolder(v) {
        abstract fun bind(images: List<Bitmap>)
    }


}
