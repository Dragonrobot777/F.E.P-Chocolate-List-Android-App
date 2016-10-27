package org.foodispower.chocolatelist.model;

import java.net.URI;

import android.os.Parcel;
import android.os.Parcelable;

public class Feature implements Parcelable {

	private String title;
	private String body;
	private String coverImageUrl;
	private String coverImageUrlRetina;
	private String articleHeaderImageUrl;
	private String articleHeaderImageUrlRetina;
	private String linkTitle;
	private String linkTarget;

	public boolean hasLink() {
	    if ( null == linkTitle || null == linkTarget) {
	        return false;
	    }

	    try {
	    	URI link = new URI( linkTarget );

	    	return null != link;
	    } catch ( Exception e ) {
	    	return false;
	    }
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCoverImageUrl() {
		return coverImageUrl;
	}
	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}
	public String getCoverImageUrlRetina() {
		return coverImageUrlRetina;
	}
	public void setCoverImageUrlRetina(String coverImageUrlRetina) {
		this.coverImageUrlRetina = coverImageUrlRetina;
	}
	public String getArticleHeaderImageUrl() {
		return articleHeaderImageUrl;
	}
	public void setArticleHeaderImageUrl(String articleHeaderImageUrl) {
		this.articleHeaderImageUrl = articleHeaderImageUrl;
	}
	public String getArticleHeaderImageUrlRetina() {
		return articleHeaderImageUrlRetina;
	}
	public void setArticleHeaderImageUrlRetina(String articleHeaderImageUrlRetina) {
		this.articleHeaderImageUrlRetina = articleHeaderImageUrlRetina;
	}
	public String getLinkTitle() {
		return linkTitle;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public String getLinkTarget() {
		return linkTarget;
	}
	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString( title );
		dest.writeString( body );
		dest.writeString( coverImageUrl );
		dest.writeString( coverImageUrlRetina );
		dest.writeString( articleHeaderImageUrl );
		dest.writeString( articleHeaderImageUrlRetina );
		dest.writeString( linkTitle );
		dest.writeString( linkTarget );
	}

	public static final Parcelable.Creator< Feature > CREATOR = new Creator< Feature >() {
	    public Feature createFromParcel( Parcel source ) {

	    	Feature feature = new Feature();

	    	feature.setTitle( source.readString() );
	    	feature.setBody( source.readString() );
	    	feature.setCoverImageUrl( source.readString() );
	    	feature.setCoverImageUrlRetina( source.readString() );
	    	feature.setArticleHeaderImageUrl( source.readString() );
	    	feature.setArticleHeaderImageUrlRetina( source.readString() );
	    	feature.setLinkTitle( source.readString() );
	    	feature.setLinkTarget( source.readString() );

	        return feature;
	    }

	    public Feature[] newArray( int size ) {
	        return new Feature[ size ];
	    }
	};
}
