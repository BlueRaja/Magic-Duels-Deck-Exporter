package com.blueraja.magicduelsimporter.carddata;

public class CardData {
    public CardData(String displayName, int idMagicDuels, int idMagicAssist) {
        this.displayName = displayName;
        this.idMagicDuels = idMagicDuels;
        this.idMagicAssist = idMagicAssist;
    }

    public String displayName;
    public int idMagicDuels;
    public int idMagicAssist;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardData cardData = (CardData) o;

        if (idMagicDuels != cardData.idMagicDuels) return false;
        if (idMagicAssist != cardData.idMagicAssist) return false;
        return displayName.equals(cardData.displayName);
    }

    @Override
    public int hashCode() {
        int result = displayName.hashCode();
        result = 31 * result + idMagicDuels;
        result = 31 * result + idMagicAssist;
        return result;
    }
}
