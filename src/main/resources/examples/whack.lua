
--[[
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
]]

local cmd = command.GetOrCreate("whack", AT_PLAYER, AT_POSITIVE_INTEGER)
cmd:SetDescription("Damages a player quickly many times in a row")

local count = 0
cmd:SetExecutor(function(sender, player, times)
	local from = "<gray>CONSOLE</gray>"
	local isSelf = true
	if (sender:IsPlayer()) then
		local senderPly = sender:ToPlayer()
		from = "<white>" .. senderPly:GetName() .. "</white>"
		if (player == nil) then
			player = senderPly
			isSelf = true
		else
			isSelf = (player:GetUUID() == senderPly:GetUUID())
		end
	else
		if (player == nil) then
			sender:SendMessage("<red>* You need to specify a player!</red>")
			return
		end
		isSelf = false
	end
	if (times == nil) then
		times = 10
	elseif (times < 1) then
		sender:SendMessage("<gray>* Did nothing</gray>")
		return
	end
	if isSelf then
		sender:SendMessage("<gold>* You whacked yourself " .. times .. " times</gold>")
	else
		sender:SendMessage("<gold>* Whacked <white>" .. player:GetName() .. "</white> " .. times .. " times</gold>")
		player:SendMessage("<gold>* " .. from .. " whacked you " .. times .. " times</gold>")
	end
	count = count + 1
	local id = "WhackTimer_" .. count
	timer.Create(id, 0.2, times, function()
		player:Damage(2)
	end )
end )
