package com.topceo.group

import com.topceo.objects.other.MyNotify

class GroupNotify(
//        var UserId: String, // "12",//MyNotify da co roi
        var RoleId: String, // "MEMBER",
//        var CreateDate: String, // "1598023782", //MyNotify da co roi
        var CreateUserId: String, // "5",
        var IsAccept: Boolean, // false,
        var NotifySettings: String, // null,
        var GroupName: String, // "Book Social",
        var CoverUrl: String, // "group/d2a819df-76da-4576-b243-4f719f187165/o.jpg",
        var UserName: String, // "moingay1trangsach",
        var AvatarSmall: String, // "https://booksocial.blob.core.windows.net/avatar/65244ec3-3cb5-44be-aa3a-cab73bbb2123/e786185b-0cd8-43d1-9bd0-408110664210/s.jpg",
        var FullName: String // "Mỗi ngày một trang sách"
) : MyNotify()