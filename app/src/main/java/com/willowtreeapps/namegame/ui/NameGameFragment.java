package com.willowtreeapps.namegame.ui;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.os.Bundle;
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
import com.willowtreeapps.namegame.core.GameProfile;
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
    private static final int GREEN = 0xff5cb85c;
    private static final int RED = 0xffb00020;

    @Inject
    ListRandomizer listRandomizer;
    @Inject
    Picasso picasso;

    @Inject
    NameGameViewModelFactory modelFactory;

    NameGameViewModel nameGameViewModel;

    private TextView title;
    private TextView statusText;
    private ViewGroup container;
    private List<ImageView> faces = new ArrayList<>(6);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
        nameGameViewModel = modelFactory.get(this);
        nameGameViewModel.getChoices().observe(this, new Observer<List<GameProfile>>() {
            @Override
            public void onChanged(@Nullable List<GameProfile> profiles) {
                if (profiles == null) {
                    return;
                }
                udpateTextStatus();
                updateImages(profiles);
            }
        });
        nameGameViewModel.getCorrectChoice().observe(this, new Observer<Person>() {
            @Override
            public void onChanged(@Nullable Person person) {
                if (person == null) {
                    return;
                }
                title.setText(String.format("Which picture is %s %s?", person.getFirstName(), person.getLastName()));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        title = view.findViewById(R.id.title);
        statusText = view.findViewById(R.id.guessResult);
        container = view.findViewById(R.id.face_container);

        int containerChildCount = container.getChildCount();
        for (int i = 0; i < containerChildCount; i++) {
            ImageView face = (ImageView) container.getChildAt(i);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!nameGameViewModel.isGameActive().getValue()) {
                        return;
                    }
                    int index = faces.indexOf(v);
                    if (index < 0) {
                        throw new IllegalStateException("View is not in faces list");
                    }

                    nameGameViewModel.submitChoice(index);
                }
            });
        }

        view.findViewById(R.id.newGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faces = new ArrayList<>();
                nameGameViewModel.newGame();
            }
        });

        nameGameViewModel.fetchData();
    }

    private void udpateTextStatus() {
        if (statusText == null) {
            return;
        }
        switch(nameGameViewModel.getOverallGameState()){
            case NotGuessed:
                statusText.setText("");
                statusText.setTextColor(Color.alpha(0));
                break;
            case CorrectGuess:
                statusText.setText(R.string.correct);
                statusText.setTextColor(GREEN);
                break;
            case IncorrectGuess:
                statusText.setText(R.string.incorrect);
                statusText.setTextColor(RED);
                break;
        }
    }

    /**
     * A method for setting the images from people into the imageviews
     */
    private void updateImages(List<GameProfile> profiles) {

        boolean newGame = false;
        if (faces.size() == 0) {
            newGame = true;
            int containerChildCount = container.getChildCount();
            for (int i = 0; i < containerChildCount; i++) {
                ImageView face = (ImageView) container.getChildAt(i);

                face.setScaleX(0);
                face.setScaleY(0);
                faces.add(face);
            }
        }

        int imageSize = (int) Ui.convertDpToPixel(100, getContext());
        int n = faces.size();

        for (int i = 0; i < n; i++) {
            ImageView face = faces.get(i);
            GameProfile profile = profiles.get(i);
            String url = profile.getPerson().getHeadshot().getUrl();

            int borderColor = Color.WHITE;
            switch(profile.getGuessState()) {
                case CorrectGuess:
                    borderColor = GREEN;
                    break;
                case IncorrectGuess:
                    borderColor = RED;
                    break;
                case NotGuessed:
                    borderColor = Color.WHITE;
                    picasso.load(url)
                            .transform(new CircleBorderTransform(RED))
                            .fetch();
                    break;
            }

            // Fix URL so that pictures will load
            if (url.startsWith("//")) {
                url = "https:" + url;
            }

            picasso.load(url)
                    .placeholder(R.drawable.ic_face_white_48dp)
                    .resize(imageSize, imageSize)
                    .transform(new CircleBorderTransform(borderColor))
                    .into(face);
        }

        if (newGame) {
            animateFacesIn();
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
}
