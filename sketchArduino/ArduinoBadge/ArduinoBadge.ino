#include <SPI.h>
#include <MFRC522.h>

#define RST_PIN		9
#define SS_PIN		10

byte readCard[4];
String ID;
int led = 8;

MFRC522 mfrc522(SS_PIN, RST_PIN);	// Create MFRC522 instance

void setup() {
  pinMode(led,OUTPUT);
  digitalWrite(led,LOW);
  Serial.begin(9600);		// Initialize serial communications with the PC
  SPI.begin();			// Init SPI bus
  mfrc522.PCD_Init();		// Init MFRC522
}

void loop() {
  getID();
}

void getID() {
  // Getting ready for Reading PICCs
  if ( ! mfrc522.PICC_IsNewCardPresent()) { //If a new PICC placed to RFID reader continue
    return;
  }
  if ( ! mfrc522.PICC_ReadCardSerial()) { //Since a PICC placed get Serial and continue
    return;
  }
  // There are Mifare PICCs which have 4 byte or 7 byte UID care if you use 7 byte PICC
  // I think we should assume every PICC as they have 4 byte UID
  // Until we support 7 byte PICCs
  //Serial.println("Scanned PICC's UID:");

  for (int i = 0; i < 4; i++) {
    readCard[i] = mfrc522.uid.uidByte[i];
    ID += String(readCard[i], HEX);
  }

  ID.toUpperCase();
  Serial.println(ID);
  if (Serial.available() && Serial.read() == 1) {
    digitalWrite(led,HIGH);
    delay(500);
    digitalWrite(led,LOW);
  }  
  ID = "";
  mfrc522.PICC_HaltA(); // Stop reading
}
