/*
Copyright (C) 2011 MoSync AB

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License,
version 2, as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
MA 02110-1301, USA.
*/

/**
 * @file MainScreen.h
 * @author emma
 */

#ifndef MAINSCREEN_H_
#define MAINSCREEN_H_

#include <maapi.h>

#include <MAUtil/util.h>

// Include all the wrappers.
#include <NativeUI/Widgets.h>

using namespace NativeUI;

class MainScreen:
	public Screen,
	public ButtonListener,
	public CheckBoxListener,
	public SliderListener
{

public:
	/**
	 * Constructor.
	 */
		MainScreen();

	/**
	 * Destructor.
	 */
	~MainScreen();

private:

    /**
     * This method is called when there is an touch-down event for
     * a button.
     * @param button The button object that generated the event.
     */
    virtual void buttonPressed(Widget* button) {};

    /**
     * This method is called when there is an touch-up event for
     * a button.
     * @param button The button object that generated the event.
     */
    virtual void buttonReleased(Widget* button) {};

    /**
     * This method is called if the touch-up event was inside the
     * bounds of the button.
     * @param button The button object that generated the event.
     */
    virtual void buttonClicked(Widget* button);

    /**
     * This method is called when the state of the check box has changed.
     * @param checkBox The check box object that generated the event.
     * @param state True if the check box is checked, false otherwise.
     */
    virtual void checkBoxStateChanged(
        CheckBox* checkBox,
        bool state);

    /**
     * This method is called when the value of the slider was modified by
     * the user.
     * @param slider The slider object that generated the event.
     * @param sliderValue The new slider's value.
     */
    virtual void sliderValueChanged(
        Slider* slider,
        const int sliderValue);

	/**
	 * Creates and adds main layout to the screen.
	 */
	void createMainLayout();

	void createDialogMainLayout();

private:
	/**
	 * Main layout.
	 */
	VerticalLayout* mMainLayout;

	Label* myGender;
	Label* myAge;

	Button* mButtonForm;

	// Widgets for dialog
	Dialog* mDialog;
	CheckBox* mGenderFemale;
	CheckBox* mGenderMale;
	Slider* mAge;
	Label* mAgeLabel;

	Button* mDialogDismiss;

};


#endif /* MAINSCREEN_H_ */
