#include "Display.hpp"

Display* display = NULL;

int lastAngle = 0;
void setup() {
  Serial.begin(115200);
  Serial.println("Starting!");
  // pt your setup code here, to run once:
  delay(1000);
  Serial.println("Start display!");
  display = new Display();
}

void loop() {
  Serial.println("Clear LCD!");
  display->Clear();
  Serial.println("Set Speed!");
  display->SetSpeed(20.1);
  display->SetDistance(1.5);
  lastAngle = (lastAngle + 20) % 360;
  Serial.println("Set compass!");
  display->SetCompass((float)lastAngle);
  Serial.println("Draw!");
  display->Draw();
  delay(2000);
}
