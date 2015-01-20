
/*Un client ne peut pas avoir plus d’une remise*/

CREATE OR REPLACE TRIGGER Client_une_remise
Before create on remise

begin

/*on regarde si le client a déja une remise*/

Select id_remise
From remise
Where :new

end;
/