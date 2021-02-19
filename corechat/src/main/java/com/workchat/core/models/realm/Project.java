package com.workchat.core.models.realm;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Project extends RealmObject implements Parcelable {
    public static final String PROJECT_MODEL = "PROJECT_MODEL";

    @PrimaryKey
    private long projectId;
    private String projectName;
    private String projectLink;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectLink() {
        return projectLink;
    }

    public void setProjectLink(String projectLink) {
        this.projectLink = projectLink;
    }

    public Project() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.projectId);
        dest.writeString(this.projectName);
        dest.writeString(this.projectLink);
    }

    protected Project(Parcel in) {
        this.projectId = in.readLong();
        this.projectName = in.readString();
        this.projectLink = in.readString();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
