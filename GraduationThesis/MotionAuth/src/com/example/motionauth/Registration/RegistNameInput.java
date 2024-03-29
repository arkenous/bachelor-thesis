package com.example.motionauth.Registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.motionauth.R;
import com.example.motionauth.Utility.LogUtil;


/**
 * ユーザに名前を入力させる
 *
 * @author Kensuke Kousaka
 */
public class RegistNameInput extends Activity {
	// ユーザが入力した文字列（名前）を格納する
	public static String name;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogUtil.log(Log.INFO);

		// タイトルバーの非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist_name_input);

		name = "";

		nameInput();
	}


	/**
	 * ユーザの名前入力を受け付ける処理
	 */
	private void nameInput () {
		LogUtil.log(Log.INFO);

		final EditText nameInput = (EditText) findViewById(R.id.nameInputEditText);

		nameInput.addTextChangedListener(new TextWatcher() {
			// 変更前
			public void beforeTextChanged (CharSequence s, int start, int count, int after) {
			}

			// 変更直前
			public void onTextChanged (CharSequence s, int start, int before, int count) {
			}

			// 変更後
			public void afterTextChanged (Editable s) {
				// ユーザの入力した名前をnameに格納
				if (nameInput.getText() != null) name = nameInput.getText().toString().trim();
			}
		});

		nameInput.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey (View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					// ソフトウェアキーボードのEnterキーを押した時，ソフトウェアキーボードを閉じる
					InputMethodManager inputMethodManager = (InputMethodManager) RegistNameInput.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					return true;
				}
				return false;
			}
		});

		// OKボタンを押した時に，次のアクティビティに移動
		final Button ok = (Button) findViewById(R.id.okButton);

		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				LogUtil.log(Log.DEBUG, "Click ok button");
				// nameが入力されているかの確認
				if (name.length() == 0) {
					Toast.makeText(RegistNameInput.this, "名前が入力されていません", Toast.LENGTH_LONG).show();
				}
				else {
					RegistNameInput.this.moveActivity("com.example.motionauth", "com.example.motionauth.Registration.RegistMotion", true);
				}
			}
		});
	}


	/**
	 * アクティビティを移動する
	 *
	 * @param pkgName 移動先のパッケージ名
	 * @param actName 移動先のアクティビティ名
	 * @param flg     戻るキーを押した際にこのアクティビティを表示させるかどうか
	 */
	private void moveActivity (String pkgName, String actName, boolean flg) {
		LogUtil.log(Log.INFO);
		Intent intent = new Intent();

		intent.setClassName(pkgName, actName);

		if (flg) intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivityForResult(intent, 0);
	}

}
