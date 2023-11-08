-- written by CynicRus, 2018
-- Just simple mod for education:)
kills = "";
alive = "";
pos = "";
time = "";
show = false;
keyTable = {}
activateKey = Keyboard.KEY_F11;

function liveInfo()
if show then
local texManager = getTextManager();
	texManager:DrawString(80.0,25.0, kills);
	texManager:DrawString(80.0,35.0, alive);
	texManager:DrawString(80.0,45.0, pos);
	texManager:DrawString(80.0,55.0, time);
end
end

function checkKey()
    if show then
		show = false
	else
		show = true
 end

end

function playerUpdate()
 local Player = getSpecificPlayer(0);
 kills = getGameTime():getZombieKilledText(Player);
 alive = getGameTime():getDeathString(Player);
 time = getGameTime():getDeathString(Player);
 local playerX = Player:getX();
 local playerY = Player:getY();
 pos = "Position: " .. round(playerX) .. " : " .. round(playerY)
 if isKeyDown(activateKey) then
	if not keyTable[activateKey] then
	  keyTable[activateKey] = true
	  checkKey()
	  end
  else
    keyTable[activateKey] = false;
 end;

end

local function round(_num)
	local number = _num;
	return number <= 0 and floor(number) or floor(number + 0.5);
end

function init()
	Events.OnPostUIDraw.Add(playerUpdate);
	Events.OnPostUIDraw.Add(liveInfo);
end

Events.OnGameStart.Add(init);






