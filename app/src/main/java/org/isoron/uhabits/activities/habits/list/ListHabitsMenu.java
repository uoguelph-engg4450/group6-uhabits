/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.activities.habits.list;

import android.support.annotation.*;
import android.view.*;

import org.isoron.uhabits.*;
import org.isoron.uhabits.models.*;
import org.isoron.uhabits.activities.*;
import org.isoron.uhabits.activities.habits.list.model.*;
import org.isoron.uhabits.models.sqlite.SQLiteHabitList;
import org.isoron.uhabits.preferences.*;
import org.isoron.uhabits.models.sqlite.SQLModelFactory;

import javax.inject.*;

@ActivityScope
public class ListHabitsMenu extends BaseMenu
{
    @NonNull
    private final ListHabitsScreen screen;

    private final HabitCardListAdapter adapter;

    private boolean showArchived;

    private boolean showCompleted;

    SQLModelFactory modelFactory;

    private boolean showSorted;

    private final Preferences preferences;

    private ThemeSwitcher themeSwitcher;

    @Inject
    public ListHabitsMenu(@NonNull BaseActivity activity,
                          @NonNull ListHabitsScreen screen,
                          @NonNull HabitCardListAdapter adapter,
                          @NonNull Preferences preferences,
                          @NonNull ThemeSwitcher themeSwitcher)
    {
        super(activity);
        this.screen = screen;
        this.adapter = adapter;
        this.preferences = preferences;
        this.themeSwitcher = themeSwitcher;

        showCompleted = preferences.getShowCompleted();
        showArchived = preferences.getShowArchived();
        showSorted = preferences.getShowSorted();
        updateAdapterFilter();
    }

    @Override
    public void onCreate(@NonNull Menu menu)
    {
        MenuItem nightModeItem = menu.findItem(R.id.actionToggleNightMode);
        nightModeItem.setChecked(themeSwitcher.isNightMode());

        MenuItem hideArchivedItem = menu.findItem(R.id.actionHideArchived);
        hideArchivedItem.setChecked(!showArchived);

        MenuItem hideCompletedItem = menu.findItem(R.id.actionHideCompleted);
        hideCompletedItem.setChecked(!showCompleted);

        MenuItem sortListItem = menu.findItem(R.id.actionSortList);
        sortListItem.setChecked(!showSorted);
    }

    @Override
    public boolean onItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.actionToggleNightMode:
                screen.toggleNightMode();
                return true;

            case R.id.actionAdd:
                screen.showCreateHabitScreen();
                return true;

            case R.id.actionFAQ:
                screen.showFAQScreen();
                return true;

            case R.id.actionAbout:
                screen.showAboutScreen();
                return true;

            case R.id.actionSettings:
                screen.showSettingsScreen();
                return true;

            case R.id.actionHideArchived:
                toggleShowArchived();
                invalidate();
                return true;
            //new action case for sorting
            case R.id.actionSortList:
                for(int i = 0; i<SQLiteHabitList.getInstance(modelFactory).size();i++ )
                    sortHabits();
                return true;

            case R.id.actionHideCompleted:
                toggleShowCompleted();
                invalidate();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected int getMenuResourceId()
    {
        return R.menu.list_habits;
    }

    private void toggleShowArchived()
    {
        showArchived = !showArchived;
        preferences.setShowArchived(showArchived);
        updateAdapterFilter();
    }

    private void toggleShowCompleted()
    {
        showCompleted = !showCompleted;
        preferences.setShowCompleted(showCompleted);
        updateAdapterFilter();
    }

    private void sortHabits()
    {
        //showSorted = !showSorted;
        preferences.setShowSorted(showSorted);

        SQLiteHabitList.getInstance(modelFactory).SortThis();
        updateAdapterFilter();

    }

    private void updateAdapterFilter()
    {
        adapter.setFilter(new HabitMatcherBuilder()
            .setArchivedAllowed(showArchived)
            .setCompletedAllowed(showCompleted)
            .build());
        adapter.refresh();
    }
}