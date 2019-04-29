package com.willowtreeapps.namegame.ui;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.Ui;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class NameGameFragment extends Fragment {

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();

    @Inject
    ListRandomizer listRandomizer;
    @Inject
    Picasso picasso;

    @Inject
    NameGameViewModelFactory modelFactory;

    NameGameViewModel nameGameViewModel;

    private TextView title;
    private ViewGroup container;
    private List<ImageView> faces = new ArrayList<>(6);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
        nameGameViewModel = modelFactory.get(this);
        nameGameViewModel.getChoices().observe(this, new Observer<List<Person>>() {
            @Override
            public void onChanged(@Nullable List<Person> profiles) {
                if (profiles == null) {
                    return;
                }
                setImages(faces, profiles);
            }
        });
        nameGameViewModel.getCorrectChoice().observe(this, new Observer<Person>() {
            @Override
            public void onChanged(@Nullable Person person) {
                if (person == null) {
                    return;
                }
                title.setText(String.format("Which picture matches %s %s?", person.getFirstName(), person.getLastName()));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        title = (TextView) view.findViewById(R.id.title);
        container = (ViewGroup) view.findViewById(R.id.face_container);

        //Hide the views until data loads
//        title.setAlpha(0);

        int n = container.getChildCount();
        for (int i = 0; i < n; i++) {
            ImageView face = (ImageView) container.getChildAt(i);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = faces.indexOf(v);
                    if (index < 0) {
                        throw new IllegalStateException("View is not in faces list");
                    }

                    if (nameGameViewModel.isCorrectChoice(index)) {
                        v.setBackgroundColor(Color.GREEN);
                        title.setText("YES!!!!");
                    }
                    else {
                        v.setBackgroundColor(Color.RED);
                        title.setText("No, try again");
                    }
                }
            });

            //Hide the views until data loads
//            face.setScaleX(0);
//            face.setScaleY(0);
            faces.add(face);
        }

        nameGameViewModel.init();
    }

    /**
     * A method for setting the images from people into the imageviews
     */
    private void setImages(@NotNull List<ImageView> faces, List<Person> profiles) {
        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            String url = profiles.get(i).getHeadshot().getUrl();

            // Fix URL so that pictures will load
            if (url.startsWith("//")) {
                url = "https:" + url;
            }

            picasso.load(url)
                    .placeholder(R.drawable.ic_face_white_48dp)
                    .resize(imageSize, imageSize)
                    .transform(new CircleBorderTransform())
                    .into(face);
        }
    }

    /**
     * A method to animate the faces into view
     */
    private void animateFacesIn() {
        title.animate().alpha(1).start();
        for (int i = 0; i < faces.size(); i++) {
            ImageView face = faces.get(i);
            face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * i).setInterpolator(OVERSHOOT).start();
        }
    }

    /**
     * A method to handle when a person is selected
     *
     * @param view   The view that was selected
     * @param person The person that was selected
     */
    private void onPersonSelected(@NonNull View view, @NonNull Person person) {
        //TODO evaluate whether it was the right person and make an action based on that
    }

}
