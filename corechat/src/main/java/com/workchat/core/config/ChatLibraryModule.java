package com.workchat.core.config;

import io.realm.annotations.RealmModule;

//@RealmModule(library = true, classes = {UserChat.class, Room.class, LastLog.class, Project.class, UserInfo.class})
@RealmModule(library = true, allClasses = true)
public class ChatLibraryModule { }
