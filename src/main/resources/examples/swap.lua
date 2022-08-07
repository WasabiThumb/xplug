
--[[
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
]]

local plys = server.GetPlayers()
local matches = {}

local len = #plys
if len > 0 then

	for k,v in pairs(plys) do
		local tk = k - 1
		if tk < 1 then
			tk = len
		end
		matches[tk] = v
		v:SendMessage("<gray>Swapping positions!</gray>")
	end

	for i=1,len,1 do
		local a = plys[i]
		local b = plys[i]
		local aPos = a:GetPos()
		local bPos = b:GetPos()
		a:TeleportAsync(bPos)
		b:TeleportAsync(aPos)
	end

end