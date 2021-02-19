package com.smartapp.sizes

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH

/**
ISize size = new SizeFromVideoFile(videoFilePath);
size.width();
size.height();
 */
class SizeFromVideoFile(var filePath: String) : ISize {
    private var width = -1
    private var height = -1

    override fun width(): Int {
        if (width == -1) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            return Integer.valueOf(
                    retriever.extractMetadata(
                            METADATA_KEY_VIDEO_WIDTH))
        }
        return width

    }

    override fun height(): Int {
        if (height == -1) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            return Integer.valueOf(
                    retriever.extractMetadata(
                            METADATA_KEY_VIDEO_HEIGHT))
        }
        return height

    }
}