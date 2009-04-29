package org.microemu.android.device.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.DateFieldUI;

import android.content.res.TypedArray;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class AndroidDateFieldUI extends LinearLayout implements DateFieldUI {

	private MicroEmulatorActivity activity;
	
	private DateField dateField;
	
	private TextView labelView;
	
	private View datetimeView;
	
	private int mode;

	public AndroidDateFieldUI(final MicroEmulatorActivity activity, final DateField dateField) {
		super(activity);
		
		this.activity = activity;
		this.dateField = dateField;
		
		this.mode = -1;
		
		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
		//		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = labelView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				labelView.setTextAppearance(labelView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				addView(labelView);
				
				setLabel(dateField.getLabel());
			}
		});
	}

	public void setLabel(String label) {
		labelView.setText(label);
	}

	public void setInputMode(final int mode) {
		activity.post(new Runnable() {
			public void run() {
				if (AndroidDateFieldUI.this.mode != mode) {
					AndroidDateFieldUI.this.mode = mode;
					if (mode == DateField.TIME) {
						datetimeView = new TimePicker(activity);
						((TimePicker) datetimeView).setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
		
							public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
								AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
								if (formList != null) {
									ItemStateListener listener = formList.getUI().getItemStateListener();
									if (listener != null) {
										listener.itemStateChanged(dateField);
									}
								}
							}
							
						});
					} else if (mode == DateField.DATE) {
						datetimeView = new DatePicker(activity);
						((DatePicker) datetimeView).init(1970, 1, 1, new DatePicker.OnDateChangedListener() {
		
							public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
								if (formList != null) {
									ItemStateListener listener = formList.getUI().getItemStateListener();
									if (listener != null) {
										listener.itemStateChanged(dateField);
									}
								}
							}
							
						});
					} else { // DateField.DATE_TIME
						System.out.println("DateField.DATE_TIME not supported yet");
					}
					datetimeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
					addView(datetimeView);
				}
			}
		});
	}

	public void setDate(final Date date) {
		activity.post(new Runnable() {
			public void run() {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(date);
				if (mode == DateField.DATE) {
					((DatePicker) datetimeView).updateDate(
							cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				}
				if (mode == DateField.TIME) {
					((TimePicker) datetimeView).setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
					((TimePicker) datetimeView).setCurrentMinute(cal.get(Calendar.MINUTE));
				}
			}
		});
	}

	public Date getDate() {
System.out.println("AndroidDateFieldUI.getDate() not synced");				
		GregorianCalendar cal = new GregorianCalendar();
		if (mode == DateField.DATE) {
			cal.set(Calendar.YEAR, ((DatePicker) datetimeView).getYear());
			cal.set(Calendar.MONTH, ((DatePicker) datetimeView).getMonth());
			cal.set(Calendar.DAY_OF_MONTH, ((DatePicker) datetimeView).getDayOfMonth());
		}
		if (mode == DateField.TIME) {
			cal.set(Calendar.HOUR_OF_DAY, ((TimePicker) datetimeView).getCurrentHour());
			cal.set(Calendar.MINUTE, ((TimePicker) datetimeView).getCurrentMinute());
		}
		
		return cal.getTime();
	}

}