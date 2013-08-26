package org.shujito.cartonbox.view.fragments.dialogs;

import org.shujito.cartonbox.Logger;
import org.shujito.cartonbox.R;
import org.shujito.cartonbox.model.Site;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AddOrEditSiteDialogFragment extends SherlockDialogFragment
	implements View.OnClickListener, DialogInterface.OnClickListener,
	OnCheckedChangeListener, OnFocusChangeListener
{
	public static final String TAG = "org.shujito.cartonbox.view.fragments.dialogs.AddSiteDialogFragment";
	public static final String EXTRA_SITE = "org.shujito.cartonbox.view.fragments.dialogs.AddSiteDialogFragment.EXTRA_SITE";
	public static final int REQUEST_CODE = ((R.string.app_name & 0xffff) ^ (R.string.app_name >> 16));
	
	public interface AddSiteDialogCallback
	{
		public void onOk(Site site);
		public void onCancel();
	}
	
	AddSiteDialogCallback callback = null;
	
	ImageButton btnAddIcon = null;
	EditText etSiteName = null;
	EditText etSiteUrl = null;
	Spinner spSiteType = null;
	CheckBox cbSiteAuthDetails = null;
	EditText etUsername = null;
	EditText etPassword = null;
	
	String[] sites = null;
	String iconFilePath = null;
	int[] siteids = null;
	
	public AddOrEditSiteDialogFragment() { }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			this.callback = (AddSiteDialogCallback)activity;
		}
		catch(Exception ex)
		{
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle cirno)
	{
		LayoutInflater inf = this.getActivity().getLayoutInflater();
		View v = inf.inflate(R.layout.dialog_addsite, null);
		
		this.btnAddIcon = (ImageButton)v.findViewById(R.id.dialog_addsite_btnaddicon);
		this.etSiteName = (EditText)v.findViewById(R.id.dialog_addsite_etname);
		this.etSiteUrl = (EditText)v.findViewById(R.id.dialog_addsite_eturl);
		this.spSiteType = (Spinner)v.findViewById(R.id.dialog_addsite_sptype);
		this.cbSiteAuthDetails = (CheckBox)v.findViewById(R.id.dialog_addsite_cbauthdetails);
		this.etUsername = (EditText)v.findViewById(R.id.dialog_addsite_etusername);
		this.etPassword = (EditText)v.findViewById(R.id.dialog_addsite_etpassword);
		
		this.btnAddIcon.setOnClickListener(this);
		this.etSiteUrl.setOnFocusChangeListener(this);
		this.cbSiteAuthDetails.setOnCheckedChangeListener(this);
		this.onCheckedChanged(this.cbSiteAuthDetails, this.cbSiteAuthDetails.isChecked());
		
		this.sites = this.getActivity().getResources().getStringArray(R.array.sitetypes);
		this.siteids = this.getActivity().getResources().getIntArray(R.array.sitetypesids);
		
		ArrayAdapter<String> arrada = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, sites);
		this.spSiteType.setAdapter(arrada);
		
		if(this.getArguments() != null)
		{
			Site site = (Site)this.getArguments().getSerializable(EXTRA_SITE);
			if(site != null)
			{
				this.etSiteName.setText(site.getName());
				this.etSiteUrl.setText(site.getUrl());
				if(site.getIcon() != null)
				{
					this.btnAddIcon.setImageURI(Uri.parse(site.getIcon()));
					this.iconFilePath = site.getIcon();
				}
			}
		}
		
		return new AlertDialog.Builder(this.getActivity())
			.setTitle("add a site...")
			.setView(v)
			.setPositiveButton(android.R.string.ok, null)
			.setNegativeButton(android.R.string.cancel, null)
			.create();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		// avoid closing the dialog when clicking the buttons by
		// stealing their listeners
		((AlertDialog)this.getDialog())
			.getButton(DialogInterface.BUTTON_POSITIVE)
			.setOnClickListener(this);
		((AlertDialog)this.getDialog())
			.getButton(DialogInterface.BUTTON_NEGATIVE)
			.setOnClickListener(this);
	}
	
	/* View.OnClickListener */
	@Override
	public void onClick(View v)
	{
		if(((AlertDialog)this.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).equals(v))
			this.onClick(this.getDialog(), DialogInterface.BUTTON_POSITIVE);
		else if(((AlertDialog)this.getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE).equals(v))
			this.onClick(this.getDialog(), DialogInterface.BUTTON_NEGATIVE);
		else if(this.btnAddIcon.equals(v))
		{
			//Intent ntn = new Intent(Intent.ACTION_VIEW, null);
			try
			{
				Intent ntn = new Intent(Intent.ACTION_PICK);
				ntn.setType("image/*");
				this.startActivityForResult(ntn, REQUEST_CODE);
				//Logger.i("AddSiteDialogFragment::onClick", String.format("code: %x", RESULT_CODE));
			}
			catch(Exception ex)
			{
				Toast.makeText(this.getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
	/* View.OnClickListener */
	
	/* DialogInterface.OnClickListener */
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		//this.etSiteName.setError(null);
		//this.etSiteUrl.setError(null);
		
		if(which == DialogInterface.BUTTON_POSITIVE)
		{
			if(this.etSiteName.length() > 0 && this.etSiteName.length() > 0)
			{
				String siteName = this.etSiteName.getText().toString();
				String siteUrl = this.etSiteUrl.getText().toString();
				// can't be left as 'http://'
				if((siteUrl.startsWith("http://") && siteUrl.length() > 7))
				{
					this.dismiss();
					int selected = this.spSiteType.getSelectedItemPosition();
					int id = this.siteids[selected];
					
					Site.Type type = Site.Type.fromInt(id);
					
					Site site = Site.createByType(type);
					
					site.setName(siteName);
					site.setUrl(siteUrl);
					// it's raw
					site.setIcon(this.iconFilePath);
					
					if(this.callback != null)
						this.callback.onOk(site);
					
				}
				else
				{
					// no
					this.etSiteUrl.requestFocus();
					this.etSiteUrl.setError(this.getText(R.string.enteravalidurl));
				}
			}
			else
			{
				if(this.etSiteUrl.length() == 0)
				{
					this.etSiteUrl.requestFocus();
					this.etSiteUrl.setError(this.getText(R.string.enteravalidurl));
				}
				if(this.etSiteName.length() == 0)
				{
					this.etSiteName.requestFocus();
					this.etSiteName.setError(this.getText(R.string.notempty));
				}
			}
			
			//SitesDB sitesdb = new SitesDB(this.getActivity());
			//sitesdb.add(site);
		}
		else if(which == DialogInterface.BUTTON_NEGATIVE)
		{
			this.dismiss();
			if(this.callback != null)
				this.callback.onCancel();
		}
	}
	/* DialogInterface.OnClickListener */
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
		{
			//Logger.i("AddSiteDialogFragment::onActivityResult", data.getData().getPath());
			try
			{
				Cursor cursor = this.getActivity().getContentResolver().query(
						data.getData(),
						new String[]{ MediaStore.Images.Media.DATA },
						null,
						null,
						null
					);
				
				if(cursor.moveToFirst())
				{
					String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					this.btnAddIcon.setImageURI(Uri.parse(filePath));
					this.iconFilePath = filePath;
				}
			}
			catch(Exception ex)
			{
				Logger.e("AddSiteDialogFragment::onActivityResult", ex.getMessage(), ex);
				//Toast.makeText(this.getActivity(), ex.toString(), Toast.LENGTH_LONG).show();
				//Toast.makeText(this.getActivity(), "Can't open that...", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public void onCancel(DialogInterface dialog)
	{
		super.onCancel(dialog);
		if(this.callback != null)
			this.callback.onCancel();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		//Toast.makeText(this.getActivity(), String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
		if(isChecked)
		{
			this.etUsername.setVisibility(View.VISIBLE);
			this.etPassword.setVisibility(View.VISIBLE);
		}
		else
		{
			this.etUsername.setVisibility(View.GONE);
			this.etPassword.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean focused)
	{
		EditText tv = (EditText)v;
		if(focused && tv.length() == 0)
		{
			tv.setText(tv.getHint());
		}
		tv.setSelection(tv.length());
	}
}
