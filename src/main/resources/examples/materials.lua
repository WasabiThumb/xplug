 
project.LogWarn("This script is meant for testing purposes and may be removed in the future")

local players = server.GetPlayers()
if (#players ~= 1) then
	project.LogWarn("Exactly 1 player must be online!")
	return
end

local ply = players[1]
if (not ply:IsOp()) then
	project.LogWarn("All players must be operators!")
	return
end

local pos = ply:GetPos()
local x = pos:GetX() + 13
local y = pos:GetY()
local z = pos:GetZ()
local world = ply:GetWorld()

local colors = { "RED", "GREEN", "BLUE", "CYAN", "MAGENTA", "YELLOW", "BLACK", "WHITE" }
z = z - (#colors)

local subjects = { "STAINED_GLASS_PANE", "STAINED_GLASS", "GLAZED_TERRACOTTA", "TERRACOTTA", "CONCRETE", "WOOL" }

local zo = 0
for _,v in ipairs(colors) do
	local target
	local block

	local go = 0
	for _,g in ipairs(subjects) do
		target = Location(world, x + go, y, z + zo)
		block = target:GetBlock()
		block:SetMaterial(Material(v .. "_" .. g))
		go = go - 2
	end

	zo = zo + 2
end
