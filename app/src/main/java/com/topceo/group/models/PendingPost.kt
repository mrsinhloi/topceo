package com.topceo.group.models

import android.os.Parcelable
import com.topceo.objects.image.ImageItem
import com.topceo.objects.image.Item
import com.topceo.objects.image.ItemData
import com.topceo.objects.other.UserShort
import com.google.gson.Gson
import com.sonhvp.utilities.standard.isNotBlankAndNotEmpty
import kotlinx.android.parcel.Parcelize

@Parcelize
class PendingPost(
        var ItemId: Long,
        var GroupId: Long,
        var UserId: Long,
        var PostId: Long,
        var CreateDate: Long,
        var Status: Int,
        var ApproveDate: Long,
        var ApproveUserId: Long,
        var RejectReason: String,
        var ImageItemId: Long,
        var ItemContent: ArrayList<Item>,
        var Description: String,
        var ItemData: String,//ItemData
        var ItemType: String,
        var Owner: UserShort
) : Parcelable {
    fun convertToImageItem(): ImageItem {
        val item = ImageItem()
        item.itemId = ItemId
        item.groupId = GroupId
        item.createDate = CreateDate
        item.imageItemId = ImageItemId
        item.itemContent = ItemContent
        item.description = Description
        item.itemType = ItemType
        item.owner = Owner
        //itemdata co the la string
        try {
            if(ItemData.isNotBlankAndNotEmpty()){
                val data = Gson().fromJson(ItemData,com.topceo.objects.image.ItemData::class.java) as ItemData
                item.itemData = data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return item
    }
}