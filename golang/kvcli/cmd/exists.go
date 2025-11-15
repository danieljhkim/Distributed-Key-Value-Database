/*
Copyright © 2025 danieljhkim
*/
package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

var existsCmd = &cobra.Command{
	Use:   "exists [key]",
	Short: "Check if key exists",
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Printf("Not yet implemented: %s\n", args[0])
		return
		response, err := kvClient.ExecuteCommand("KV EXISTS " + args[0])
		if err != nil {
			fmt.Printf("Error: %v\n", err)
			return
		}
		fmt.Println(response)
	},
}

func init() {
	// rootCmd.AddCommand(existsCmd)
}
