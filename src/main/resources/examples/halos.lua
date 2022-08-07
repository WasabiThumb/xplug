
--[[
  This Source Code Form is subject to the terms of the Mozilla Public
  License, v. 2.0. If a copy of the MPL was not distributed with this
  file, You can obtain one at http://mozilla.org/MPL/2.0/.
]]

local function halo(v)
	local pos = v:GetPos()
	local world = v:GetWorld()
	local x = pos:GetX()
	local y = pos:GetY() + 2.2
	local z = pos:GetZ()
	local radius = 0.5
	for t=0,2,0.05 do
		local px = x + radius * math.cos(math.pi * t)
		local pz = z + radius * math.sin(math.pi * t)
		particle.Start("REDSTONE", Location(world, px, y, pz))
		particle.SetOffset(0, 0, 0)
		particle.SetColor(255, 255, 0)
		particle.End()
	end
end

hook.Add("PlayerMove", "DrawHalo_Move", function(ply)
	halo(ply)
end )

timer.Create("DrawHalo", 0.3, 0, function()
	for _,v in pairs(server.GetPlayers()) do
		halo(v)
	end
end )

function stopHalo()
	hook.Remove("PlayerMove", "DrawHalo_Move")
	timer.Remove("DrawHalo")
end