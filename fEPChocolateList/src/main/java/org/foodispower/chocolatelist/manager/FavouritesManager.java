package org.foodispower.chocolatelist.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.foodispower.chocolatelist.model.Company;

import android.content.Context;

public class FavouritesManager {
	
	private static FavouritesManager favouritesManager;
	
	public static final String FILENAME = "favourites";

	private Context context;
	private ArrayList< String > favourites;

	static public FavouritesManager getInstance( Context context ) {
		if ( null == favouritesManager ) {
			favouritesManager = new FavouritesManager( context );
		}

		return favouritesManager;
	}

	public FavouritesManager( Context context ) {
		this.context = context;
		read();
	}

	private void read() {
		ArrayList< String > favourites = null;
        try{
            FileInputStream fis = context.openFileInput( FILENAME );
            ObjectInputStream ois = new ObjectInputStream( fis );
            @SuppressWarnings("unchecked")
			ArrayList<String> object = ( ArrayList<String> ) ois.readObject();
			favourites = object;
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch( IOException e ){
            e.printStackTrace();
        }catch( ClassNotFoundException e ){
            e.printStackTrace();
        }
		
		if ( null == favourites ) {
			favourites = new ArrayList< String >();
		}

		this.favourites = favourites;
	}
	
	private void write() {
        try {
        	FileOutputStream fos = context.openFileOutput( FILENAME, Context.MODE_PRIVATE );
            ObjectOutputStream oos = new ObjectOutputStream( fos );
            oos.writeObject( favourites ); 
            oos.close();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch( IOException e ){
            e.printStackTrace();
        }
	}

	public Boolean isCompanyFavourited( Company company ) {
		return favourites.contains( company.getName() );
	}

	public void addFavouriteCompany( Company company ) {
		if ( !isCompanyFavourited( company )) {
			favourites.add( company.getName() );
		}

		write();
	}
	
	public void removeFavouriteCompany( Company company ) {
		if ( isCompanyFavourited( company )) {
			favourites.remove( company.getName() );
		}

		write();
	}
}
