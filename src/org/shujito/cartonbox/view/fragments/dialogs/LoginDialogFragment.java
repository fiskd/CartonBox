package org.shujito.cartonbox.view.fragments.dialogs;

import org.shujito.cartonbox.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class LoginDialogFragment extends SherlockDialogFragment
	implements DialogInterface.OnClickListener,
	OnEditorActionListener, View.OnClickListener
{
	public final static String TAG = "org.shujito.cartonbox.view.fragments.dialogs.LoginDialogFragment";
	
	public interface LoginDialogCallback
	{
		public void onFinishEditLogin(String username, String password);
		public void onCancel();
	}
	
	LoginDialogCallback mLoginDialogCallback = null;
	
	EditText tvUsername = null;
	EditText tvPassword = null;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			this.mLoginDialogCallback = (LoginDialogCallback)activity;
		}
		catch(Exception ex)
		{
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle cirno)
	{
		LayoutInflater inf = LayoutInflater.from(this.getActivity());
		View v = inf.inflate(R.layout.dialog_login, null);
		
		this.tvUsername = (EditText)v.findViewById(R.id.dialog_login_tvusername);
		this.tvUsername.requestFocus();
		this.tvPassword = (EditText)v.findViewById(R.id.dialog_login_tvpassword);
		this.tvPassword.setOnEditorActionListener(this);
		
		this.tvUsername.post(new Runnable()
		{
			@Override
			public void run()
			{
				tvUsername.requestFocus();
				InputMethodManager imm = (InputMethodManager)getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(tvUsername, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		
		return new AlertDialog.Builder(this.getActivity())
			.setTitle(R.string.login_details)
			.setMessage(R.string.login_message)
			.setPositiveButton(android.R.string.ok, null)
			.setNegativeButton(android.R.string.cancel, null)
			.setView(v)
			.create();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		((AlertDialog)this.getDialog())
			.getButton(DialogInterface.BUTTON_POSITIVE)
			.setOnClickListener(this); // I got your listener
		((AlertDialog)this.getDialog())
			.getButton(DialogInterface.BUTTON_NEGATIVE)
			.setOnClickListener(this); // also you
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if(actionId == EditorInfo.IME_ACTION_DONE)
		{
			this.onClick(this.getDialog(), DialogInterface.BUTTON_POSITIVE);
		}
		return true;
	}
	
	/* View.OnClickListener */
	@Override
	public void onClick(View v)
	{
		// fuck hacks, I hate them, sometimes I like them
		// and this one, for example, I just don't know
		if(((AlertDialog)this.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).equals(v))
			this.onClick(this.getDialog(), DialogInterface.BUTTON_POSITIVE);
		else if(((AlertDialog)this.getDialog()).getButton(DialogInterface.BUTTON_NEGATIVE).equals(v))
			this.onClick(this.getDialog(), DialogInterface.BUTTON_NEGATIVE);
		// nevermind what the fuck: --- ↑
	}
	/* View.OnClickListener */
	
	/* DialogInterface.OnClickListener methods */
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == DialogInterface.BUTTON_POSITIVE)
		{
			if(this.mLoginDialogCallback != null)
			{
				String username = this.tvUsername.getText().toString();
				String password = this.tvPassword.getText().toString();
				if((username != null && username.length() > 0) && (password != null && password.length() > 0))
				{
					this.dismiss();
					this.mLoginDialogCallback.onFinishEditLogin(username, password);
				}
				else
				{
					if(password != null && password.length() == 0)
					{
						this.tvPassword.requestFocus();
						this.tvPassword.setHintTextColor(0xffcc0000);
					}
					if(username != null && username.length() == 0)
					{
						this.tvUsername.requestFocus();
						this.tvUsername.setHintTextColor(0xffcc0000);
					}
				}
			}
		}
		if(which == DialogInterface.BUTTON_NEGATIVE)
		{
			if(this.mLoginDialogCallback != null)
				this.mLoginDialogCallback.onCancel();
		}
	}
	/* DialogInterface.OnClickListener methods */
	
	@Override
	public void onCancel(DialogInterface dialog)
	{
		super.onCancel(dialog);
		if(this.mLoginDialogCallback != null)
			this.mLoginDialogCallback.onCancel();
	}
}
