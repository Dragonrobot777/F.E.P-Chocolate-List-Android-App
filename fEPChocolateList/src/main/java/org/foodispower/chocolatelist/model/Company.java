package org.foodispower.chocolatelist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Company implements Parcelable {

	public enum CompanyStatus {
		RECOMMENDED,
		CANNOT_RECOMMEND,
	};

	public enum CompanyStatusReason {
		RECOMMENDED,
		RECOMMENDED_BENEFIT_OF_THE_DOUBT,
		CANNOT_RECOMMEND,
		CANNOT_RECOMMEND_DID_NOT_DISCLOSE,
		CANNOT_RECOMMEND_DID_NOT_RESPOND,
		CANNOT_RECOMMEND_WORKING_ON_ISSUES,
		CANNOT_RECOMMEND_RESPONDED,
	};

	private String name;
	private String notes;
	private CompanyStatus status;
	private CompanyStatusReason statusReason;
	private String logoUrl;
	private String logoUrlRetina;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public CompanyStatus getStatus() {
		return status;
	}

	public void setStatus(CompanyStatus status) {
		this.status = status;
	}

	public CompanyStatusReason getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(CompanyStatusReason statusReason) {
		this.statusReason = statusReason;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getLogoUrlRetina() {
		return logoUrlRetina;
	}

	public void setLogoUrlRetina(String logoUrlRetina) {
		this.logoUrlRetina = logoUrlRetina;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel( Parcel dest, int flags ) {
		dest.writeString( name );
		dest.writeString( notes );
		dest.writeString( null == status ? null : status.name() );
		dest.writeString( null == statusReason ? null : statusReason.name() );
		dest.writeString( logoUrl );
		dest.writeString( logoUrlRetina );
	}

	public static final Parcelable.Creator< Company > CREATOR = new Creator< Company >() {
	    public Company createFromParcel( Parcel source ) {

	    	Company company = new Company();

	    	company.setName( source.readString() );
	    	company.setNotes( source.readString() );
	    	company.setStatus( CompanyStatus.valueOf( source.readString() ) );
	    	company.setStatusReason( CompanyStatusReason.valueOf( source.readString() ) );
	    	company.setLogoUrl( source.readString() );
	    	company.setLogoUrlRetina( source.readString() );

	        return company;
	    }

	    public Company[] newArray( int size ) {
	        return new Company[ size ];
	    }
	};
}
