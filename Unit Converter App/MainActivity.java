package com.firstapp.unitconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView headingTextView;
    private EditText inputValue;
    private Spinner categorySpinner, fromUnitSpinner, toUnitSpinner;
    private Button convertButton;
    private TextView resultTextView;

    private String[] lengthUnits = {"Centimeter", "Meter", "Kilometer", "Mile", "Yard", "Foot"};
    private String[] massUnits = {"Gram", "Kilogram", "Pound", "Ounce"};
    private String[] temperatureUnits = {"Celsius", "Fahrenheit", "Kelvin"};
    private String[] timeUnits = {"Second", "Minute", "Hour", "Day"};
    private String[] speedUnits = {"Meter per second", "Kilometer per hour", "Mile per hour"};

    private final double[] METERS_CONVERSION = {100, 1.0, 0.001, 0.000621371, 1.09361, 3.28084};
    private final double[] GRAMS_CONVERSION = {1.0, 0.001, 0.00220462, 0.035274};
    private final double[] CELSIUS_CONVERSION = {1.0, 1.8, 273.15};
    private final double[] MPS_CONVERSION = {1.0, 3.6, 2.23694};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headingTextView = findViewById(R.id.heading);
        inputValue = findViewById(R.id.input_value);
        categorySpinner = findViewById(R.id.category_spinner);
        fromUnitSpinner = findViewById(R.id.from_unit);
        toUnitSpinner = findViewById(R.id.to_unit);
        convertButton = findViewById(R.id.convert_button);
        resultTextView = findViewById(R.id.result);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void updateUnitSpinners(int categoryIndex) {
        ArrayAdapter<CharSequence> unitAdapter;
        switch (categoryIndex) {
            case 0: // Length
                unitAdapter = ArrayAdapter.createFromResource(this,
                        R.array.length_units, android.R.layout.simple_spinner_item);
                break;
            case 1: // Mass
                unitAdapter = ArrayAdapter.createFromResource(this,
                        R.array.mass_units, android.R.layout.simple_spinner_item);
                break;
            case 2: // Temperature
                unitAdapter = ArrayAdapter.createFromResource(this,
                        R.array.temperature_units, android.R.layout.simple_spinner_item);
                break;
            case 3: // Time
                unitAdapter = ArrayAdapter.createFromResource(this,
                        R.array.time_units, android.R.layout.simple_spinner_item);
                break;
            case 4: // Speed
                unitAdapter = ArrayAdapter.createFromResource(this,
                        R.array.speed_units, android.R.layout.simple_spinner_item);
                break;
            default:
                return;
        }
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromUnitSpinner.setAdapter(unitAdapter);
        toUnitSpinner.setAdapter(unitAdapter);
    }

    private void performConversion() {
        String inputStr = inputValue.getText().toString();
        if (inputStr.isEmpty()) {
            resultTextView.setText("Please enter a value");
            return;
        }

        double input = Double.parseDouble(inputStr);
        int categoryIndex = categorySpinner.getSelectedItemPosition();
        int fromUnitIndex = fromUnitSpinner.getSelectedItemPosition();
        int toUnitIndex = toUnitSpinner.getSelectedItemPosition();

        double result;
        switch (categoryIndex) {
            case 0: // Length
                double meters = input / METERS_CONVERSION[fromUnitIndex];
                result = meters * METERS_CONVERSION[toUnitIndex];
                break;
            case 1: // Mass
                double grams = input / GRAMS_CONVERSION[fromUnitIndex];
                result = grams * GRAMS_CONVERSION[toUnitIndex];
                break;
            case 2: // Temperature
                result = convertTemperature(input, fromUnitIndex, toUnitIndex);
                break;
            case 3: // Time
                result = convertTime(input, fromUnitIndex, toUnitIndex);
                break;
            case 4: // Speed
                double metersPerSec = input / MPS_CONVERSION[fromUnitIndex];
                result = metersPerSec * MPS_CONVERSION[toUnitIndex];
                break;
            default:
                resultTextView.setText("Invalid category");
                return;
        }

        resultTextView.setText(String.format("%.4f", result));
    }
    private double convertTime(double value, int fromIndex, int toIndex) {
        // Convert from the source unit to seconds
        double seconds;
        switch (fromIndex) {
            case 0: // Second
                seconds = value;
                break;
            case 1: // Minute
                seconds = value * 60;
                break;
            case 2: // Hour
                seconds = value * 3600;
                break;
            case 3: // Day
                seconds = value * 86400;
                break;
            default:
                throw new IllegalArgumentException("Invalid time unit");
        }

        // Convert from seconds to the target unit
        switch (toIndex) {
            case 0: // Second
                return seconds;
            case 1: // Minute
                return seconds / 60;
            case 2: // Hour
                return seconds / 3600;
            case 3: // Day
                return seconds / 86400;
            default:
                throw new IllegalArgumentException("Invalid time unit");
        }
    }

    private double convertTemperature(double value, int fromIndex, int toIndex) {
        // Convert from the source unit to Celsius
        double celsius;
        switch (fromIndex) {
            case 0: // Celsius
                celsius = value;
                break;
            case 1: // Fahrenheit
                celsius = (value - 32) / 1.8;
                break;
            case 2: // Kelvin
                celsius = value - 273.15;
                break;
            default:
                throw new IllegalArgumentException("Invalid temperature unit");
        }

        // Convert from Celsius to the target unit
        switch (toIndex) {
            case 0: // Celsius
                return celsius;
            case 1: // Fahrenheit
                return celsius * 1.8 + 32;
            case 2: // Kelvin
                return celsius + 273.15;
            default:
                throw new IllegalArgumentException("Invalid temperature unit");
        }
    }
}
