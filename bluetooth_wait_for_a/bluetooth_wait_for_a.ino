/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
 
  This example code is in the public domain.
 */
 
// Pin 13 has an LED connected on most Arduino boards.
// give it a name:
int led = 13;
int pingtime=200;
bool good=false;

// the setup routine runs once when you press reset:
void setup() {                
  // initialize the digital pin as an output.
  pinMode(led, OUTPUT);  
  Serial.begin(9600);
  while(Serial.available()==0)
  {
    Serial.print('a');
    good=true;
     delay(50);
    digitalWrite(led, LOW);
    delay(50);
    digitalWrite(led, HIGH); 
  }  
}

// the loop routine runs over and over again forever:
void loop() {
  if (Serial.available()>0)
  {
    if (Serial.read()=='a')
    {
      good=true;
    }
    else
    {
      good=false;
    }
    delay(100);
    digitalWrite(led, LOW);
    delay(100);
    digitalWrite(led, HIGH);   // turn the LED on (HIGH is the voltage level)
  }
  while (!good || Serial.available()==0)
  {
  digitalWrite(led, LOW);    // turn the LED off by making the voltage LOW:w
  delay(1000);
  }
}
